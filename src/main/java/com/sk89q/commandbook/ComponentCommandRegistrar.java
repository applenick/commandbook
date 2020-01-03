package com.sk89q.commandbook;

import com.sk89q.worldedit.internal.command.CommandRegistrationHandler;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandManagerService;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.SubCommandPart;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ComponentCommandRegistrar {
    private CommandManagerService service;
    private CommandManager topLevelCommandManager;
    private CommandRegistrationHandler registration;

    public ComponentCommandRegistrar(CommandManagerService service, CommandManager topLevelCommandManager, CommandRegistrationHandler registration) {
        this.service = service;
        this.topLevelCommandManager = topLevelCommandManager;
        this.registration = registration;
    }

    public void registerAsSubCommand(String command, String description, CommandManager parentManager, BiConsumer<CommandManager, CommandRegistrationHandler> op) {
        parentManager.register(command, builder -> {
            builder.description(TextComponent.of(description));

            CommandManager manager = service.newCommandManager();
            op.accept(manager, registration);

            builder.addPart(SubCommandPart.builder(TranslatableComponent.of("worldedit.argument.action"), TextComponent.of("Sub-command to run."))
                    .withCommands(manager.getAllCommands().collect(Collectors.toList()))
                    .required()
                    .build());
        });
    }

    public void registerTopLevelCommands(BiConsumer<CommandManager, CommandRegistrationHandler> op) {
        CommandManager componentManager = service.newCommandManager();
        op.accept(componentManager, registration);
        topLevelCommandManager.registerManager(componentManager);
    }

    @Deprecated
    public <T> void registerConverter(Key<T> key, ArgumentConverter<T> converter) {
        topLevelCommandManager.registerConverter(key, converter);
    }
}