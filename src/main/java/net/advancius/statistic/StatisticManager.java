package net.advancius.statistic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.statistic.adapter.ClassTypeAdapterFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@FlagManager.FlaggedClass
public class StatisticManager {

    public static final String FILE_ENDING = "json";

    private static StatisticManager INSTANCE;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 0)
    private static void loadStatisticManager() {
        INSTANCE = new StatisticManager();
        INSTANCE.loadStatistics();
        AdvanciusBungee.getInstance().setStatisticManager(INSTANCE);
    }

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_SAVE, priority = Integer.MAX_VALUE)
    private static void saveStatisticManager() {
        INSTANCE.saveStatistics();
        INSTANCE = null;
        AdvanciusBungee.getInstance().setStatisticManager(INSTANCE);
    }

    private final List<Statistic> statisticList = new ArrayList<>();

    public <T> Statistic<T> registerStatistic(String namespace, String name, Class<T> statisticClass) {
        return registerStatistic(new StatisticName(namespace, name), statisticClass);
    }

    public <T> Statistic<T> registerStatistic(StatisticName name, Class<T> statisticClass) {
        return registerStatistic(new Statistic(name, statisticClass, new ArrayList<>()));
    }

    public <T> Statistic<T> registerStatistic(Statistic statistic) {
        if (hasStatistic(statistic.getName())) return getStatistic(statistic.getName());

        statisticList.add(statistic);
        AdvanciusLogger.info("Registered statistic: " + statistic.getName());
        return statistic;
    }

    public boolean hasStatistic(StatisticName name) { return getStatistic(name) != null; }

    public Statistic getStatistic(String namespace, String name) {
        return getStatistic(new StatisticName(namespace, name));
    }

    public Statistic getStatistic(StatisticName name) {
        for (Statistic statistic : statisticList) if (statistic.getName().equals(name)) return statistic;
        return null;
    }

    private void loadStatistics() {
        AdvanciusLogger.info("Loading statistics...");
        File statisticsDirectory = FileManager.getServerFile("statistics");
        statisticsDirectory.mkdirs();

        for (File namespaceDirectory : statisticsDirectory.listFiles(File::isDirectory)) {
            for (File statisticFile : namespaceDirectory.listFiles((file, name) -> name.endsWith('.' + FILE_ENDING))) {
                try {
                    FileReader fileReader = new FileReader(statisticFile);
                    Statistic statistic = generateCustomGSON().fromJson(fileReader, Statistic.class);
                    registerStatistic(statistic);

                    AdvanciusLogger.info("Successfully loaded statistic: " + statistic.getName());
                } catch (FileNotFoundException exception) {}
            }
        }
    }

    private void saveStatistics() {
        AdvanciusLogger.info("Saving " + statisticList.size() + " statistics...");

        for (Statistic statistic : new ArrayList<>(statisticList)) {
            File namespaceDirectory = FileManager.getServerFile("statistics/" + statistic.getName().getNamespace());
            namespaceDirectory.mkdirs();

            try {
                File statisticFile = new File(namespaceDirectory, statistic.getName().getName() + '.' + FILE_ENDING);
                statisticFile.createNewFile();

                FileWriter fileWriter = new FileWriter(statisticFile, false);
                fileWriter.write(generateCustomGSON().toJson(statistic));

                fileWriter.flush();
                fileWriter.close();

                statisticList.remove(statistic);
                AdvanciusLogger.info("Successfully saved statistic: " + statistic.getName());
            } catch (Exception exception) {
                AdvanciusLogger.error("Failed to save statistic: " + statistic.getName(), exception);
            }
        }
    }

    private static Gson generateCustomGSON() {
        return new GsonBuilder().registerTypeAdapterFactory(new ClassTypeAdapterFactory()).create();
    }
}
