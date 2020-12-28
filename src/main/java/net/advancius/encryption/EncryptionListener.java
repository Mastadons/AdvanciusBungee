package net.advancius.encryption;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;

@FlagManager.FlaggedClass
public class EncryptionListener implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new EncryptionListener());
    }

    @CommunicationHandler(code = Protocol.CLIENT_ENCRYPTION)
    public void onClientEncryption(Client client, CommunicationPacket communicationPacket) {
        AdvanciusLogger.info("Received unique symmetric encryption key.");
        AsymmetricEncryption.AsymmetricEncryptionKeypair encryptionKeypair = AdvanciusBungee.getInstance().getCommunicationManager().getEncryptionKeypair();

        String keyEncryptedBase64 = communicationPacket.getMetadata().getMetadata("encryption_key");
        String saltEncryptedBase64 = communicationPacket.getMetadata().getMetadata("salt");

        byte[] keyEncrypted = Base64.getDecoder().decode(keyEncryptedBase64);
        byte[] saltEncrypted = Base64.getDecoder().decode(saltEncryptedBase64);

        byte[] salt = AsymmetricEncryption.decrypt(saltEncrypted, encryptionKeypair.getPrivateKey());
        SecretKey encryptionKey = SymmetricEncryption.decodeSecretKey(AsymmetricEncryption.decrypt(keyEncrypted, encryptionKeypair.getPrivateKey()));

        client.setSalt(salt);
        client.setEncryptionKey(encryptionKey);

        CommunicationPacket encryptionAccept = CommunicationPacket.generatePacket(Protocol.SERVER_ACCEPT_ENCRYPTION);
        client.sendPacket(encryptionAccept);
    }

    private byte[] convertByteList(ArrayList<Number> byteList) {
        byte[] array = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            array[i] = byteList.get(i).byteValue();
        }
        return array;
    }
}
