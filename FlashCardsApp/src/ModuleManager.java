// ModuleManager.java
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private List<Module> modules;
    private static final String SAVE_DIRECTORY = "flashcards";

    public ModuleManager() {
        modules = new ArrayList<>();
        loadModules();
        migrateImagesToStructuredStorage(); // Migrate any old-format modules
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

    // In ModuleManager.java
    public void migrateImagesToStructuredStorage() {
        boolean needsSave = false;

        for (Module module : modules) {
            String moduleName = module.getName();
            for (Card card : module.getCards()) {
                // Ensure the card knows which module it belongs to
                if (card.getModuleName() == null || card.getModuleName().isEmpty()) {
                    card.setModuleName(moduleName);
                    needsSave = true;
                }

                if (card.getAnswerType() == Card.AnswerType.IMAGE) {
                    // This will trigger the migration logic if needed
                    try {
                        card.migrateFromOldFormat();
                        needsSave = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (needsSave) {
            saveModules(); // Save modules after migration
        }
    }

    public void removeModuleByName(String name) {
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getName().equals(name)) {
                modules.remove(i);

                // Delete the serialized file from disk
                File moduleFile = new File(SAVE_DIRECTORY + File.separator + name + ".ser");
                if (moduleFile.exists()) {
                    boolean deleted = moduleFile.delete();
                    System.out.println("Module file deleted: " + deleted + " - " + moduleFile.getAbsolutePath());

                    // Try force deletion if normal deletion fails
                    if (!deleted) {
                        try {
                            Files.deleteIfExists(moduleFile.toPath());
                            System.out.println("Module file deleted using Files.deleteIfExists");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Module file not found: " + moduleFile.getAbsolutePath());
                }

                // Save the updated modules list (without this module)
                saveModules();
                return;
            }
        }
    }
}