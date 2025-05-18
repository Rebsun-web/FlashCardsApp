// ModuleManager.java
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private List<Module> modules;
    private static final String SAVE_DIRECTORY = "flashcards";

    public ModuleManager() {
        modules = new ArrayList<>();
        loadModules();
    }

    public List<Module> getModules() {
        return modules;
    }

    public void addModule(Module module) {
        modules.add(module);
        saveModules();
    }

    public void removeModule(int index) {
        if (index >= 0 && index < modules.size()) {
            modules.remove(index);
            saveModules();
        }
    }

    public void saveModules() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        for (Module module : modules) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(SAVE_DIRECTORY + File.separator + module.getName() + ".ser"))) {
                oos.writeObject(module);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadModules() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                Module module = (Module) ois.readObject();
                modules.add(module);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }
}