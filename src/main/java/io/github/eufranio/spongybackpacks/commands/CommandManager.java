package io.github.eufranio.spongybackpacks.commands;

import com.google.common.collect.Lists;
import io.github.eufranio.spongybackpacks.SpongyBackpacks;
import io.github.eufranio.spongybackpacks.backpack.Backpack;
import io.github.eufranio.spongybackpacks.data.DataManager;
import io.github.eufranio.spongybackpacks.util.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

/**
 * Created by Frani on 13/02/2018.
 */
public class CommandManager {

    public static void registerCommands() {
        CommandSpec create = CommandSpec.builder()
                .permission("spongybackpacks.command.create")
                .extendedDescription(Text.of("Creates a new backpack for you. May error",
                        Text.NEW_LINE, "if you have more backpacks than the server limit."))
                .arguments(
                        GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false),
                        GenericArguments.integer(Text.of("rows")),
                        GenericArguments.userOrSource(Text.of("user"))
                )
                .executor((sender, ctx) -> {
                    User user = ctx.<User>getOne("user").get();
                    int slots = ctx.<Integer>getOne("rows").get();
                    if (slots < 1 || slots > 6) {
                        throw new CommandException(Text.of("The size must be higher than 0 and up to 6!"));
                    }

                    if (!(sender instanceof ConsoleSource)) {
                        int rowLimit = PlayerUtil.getIntOption(sender, "limit.rows");
                        rowLimit = rowLimit != -1 ? rowLimit : 2;
                        if (slots > rowLimit) {
                            throw new CommandException(Text.of("You can only create a " + rowLimit + " rows backpack!"));
                        }
                    }

                    if (!(sender instanceof ConsoleSource)) {
                        int packLimit = PlayerUtil.getIntOption(sender, "limit.backpacks");
                        packLimit = packLimit != -1 ? packLimit : 2;
                        if (DataManager.getBackpacks(user.getUniqueId()).size() >= packLimit) {
                            throw new CommandException(Text.of("You can only have " + packLimit + " backpacks!"));
                        }
                    }

                    Text name = ctx.<Text>getOne("name").get();
                    if (DataManager.getBackpack(name.toPlain(), user.getUniqueId()) != null) {
                        throw new CommandException(Text.of("There's already an Backpack with this name!"));
                    }

                    Backpack pack = Backpack.of(name, user.getUniqueId(), slots);
                    DataManager.getDataFor(user.getUniqueId()).addBackpack(pack);
                    sender.sendMessage(Text.of(TextColors.GREEN, " Successfully created backpack!"));
                    return CommandResult.success();
                })
                .build();

        CommandSpec open = CommandSpec.builder()
                .permission("spongybackpacks.command.open.own")
                .extendedDescription(Text.of("Opens a certain backpack for you or for",
                        Text.NEW_LINE, "a specific user, if defined"))
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("user"))
                        )
                )
                .executor((sender, ctx) -> {
                    User user;
                    if (ctx.<User>getOne("user").isPresent()) {
                        if (!sender.hasPermission("spongybackpacks.command.open.others")) {
                            throw new CommandException(Text.of("You don't have permission to open other player's backpacks!"));
                        }
                        user = ctx.<User>getOne("user").get();
                    } else {
                        if (!(sender instanceof Player)) {
                            throw new CommandException(Text.of("You cannot execute this command from console!"));
                        }
                        user = (User) sender;
                    }

                    Backpack pack = DataManager.getBackpack(ctx.<String>getOne("name").get(), user.getUniqueId());
                    if (pack == null) {
                        throw new CommandException(Text.of("This backpack doesn't exist!"));
                    }

                    if (pack.getInventory() != null) {
                        throw new CommandException(Text.of("This backpack is already being viewed!"));
                    }

                    pack.open((Player) sender);
                    sender.sendMessage(Text.of(TextColors.GRAY, " Opening your backpack..."));
                    return CommandResult.success();
                })
                .build();

        CommandSpec close = CommandSpec.builder()
                .permission("spongybackpacks.command.close")
                .extendedDescription(Text.of("Closes the invetory of the specified player"))
                .arguments(GenericArguments.player(Text.of("player")))
                .executor((sender, ctx) -> {
                    Player player = ctx.<Player>getOne("player").get();
                    player.closeInventory();
                    sender.sendMessage(Text.of(TextColors.GREEN, " Closed " + player.getName() + "'s inventory"));
                    return CommandResult.success();
                })
                .build();

        CommandSpec delete = CommandSpec.builder()
                .permission("spongybackpacks.command.delete.own")
                .extendedDescription(Text.of("Deletes one of your backpacks of from a specified",
                        Text.NEW_LINE, "user, if defined. Drop items is a boolean, use it to",
                        Text.NEW_LINE, "define if you want to drop or void the item from",
                        Text.NEW_LINE, "the backpack"))
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.bool(Text.of("drop items")),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("user"))
                        )
                )
                .executor((sender, ctx) -> {
                    User user;
                    if (ctx.<User>getOne("user").isPresent()) {
                        if (!sender.hasPermission("spongybackpacks.command.delete.others")) {
                            throw new CommandException(Text.of("You don't have permission to delete other player's backpacks!"));
                        }
                        user = ctx.<User>getOne("user").get();
                    } else {
                        if (!(sender instanceof Player)) {
                            throw new CommandException(Text.of("You must specify an player when using this command from console!"));
                        }
                        user = (User) sender;
                    }

                    Backpack pack = DataManager.getBackpack(ctx.<String>getOne("name").get(), user.getUniqueId());
                    if (pack == null) {
                        throw new CommandException(Text.of("This backpack doesn't exist!"));
                    }

                    pack.delete(sender, ctx.<Boolean>getOne("drop items").orElse(false));
                    sender.sendMessage(Text.of(TextColors.GREEN, " Succesfully deleted this backpack!"));
                    return CommandResult.success();
                })
                .build();

        CommandSpec list = CommandSpec.builder()
                .permission("spongybackpacks.command.list.own")
                .extendedDescription(Text.of("Lists your backpacks or the ones from the",
                        Text.NEW_LINE, "specific user, if defined"))
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("user"))
                        )
                )
                .executor((sender, ctx) -> {
                    User user;
                    if (ctx.<User>getOne("user").isPresent()) {
                        if (!sender.hasPermission("spongybackpacks.command.list.others")) {
                            throw new CommandException(Text.of("You don't have permission to list other player's backpacks!"));
                        }
                        user = ctx.<User>getOne("user").get();
                    } else {
                        if (!(sender instanceof Player)) {
                            throw new CommandException(Text.of("You must specify a player when running this command from console!"));
                        }
                        user = (User) sender;
                    }

                    List<Text> backpacks = Lists.newArrayList();
                    for (Backpack pack : DataManager.getBackpacks(user.getUniqueId())) {
                        Text openText = Text.of(TextColors.GREEN, "[OPEN]")
                                .toBuilder()
                                .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click to open this backpack")))
                                .onClick(TextActions.runCommand("/spongybackpacks open " + pack.getNamePlain()))
                                .build();

                        Text deleteText = Text.of(TextColors.RED, "[DELETE]")
                                .toBuilder()
                                .onHover(TextActions.showText(Text.of(TextColors.RED, "Deletes this backpack, dropping the items near you")))
                                .onClick(TextActions.runCommand("/spongybackpacks delete " + pack.getNamePlain() + " true"))
                                .build();

                        Text t = Text.of(
                                TextColors.GRAY, " Name: ", pack.getName(),
                                TextColors.GRAY, " | Rows: ", TextColors.GOLD, pack.getSize(),
                                TextColors.GRAY, " | ",
                                openText, " ", deleteText);

                        backpacks.add(t);
                    }

                    PaginationList.builder()
                            .title(Text.of(TextColors.BLUE, "Your Backpacks"))
                            .padding(Text.of(TextStyles.STRIKETHROUGH, "-"))
                            .contents(backpacks)
                            .sendTo(sender);

                    return CommandResult.success();
                })
                .build();

        CommandSpec backpack = CommandSpec.builder()
                .permission("spongybackpacks.command.main")
                .executor((sender, ctx) -> {
                    Text prefix = Text.of(TextColors.GRAY, " - ", TextColors.GREEN, "/sb ");
                    List<Text> text = Lists.newArrayList(
                            Text.of(prefix, TextColors.GREEN, "create <backpack name> <rows, 1 to 6> [<user>]")
                                    .toBuilder()
                                    .onHover(TextActions.showText(create.getExtendedDescription(sender).get()))
                                    .build(),

                            Text.of(prefix, TextColors.GREEN, "open <backpack name> [<user>]")
                                    .toBuilder()
                                    .onHover(TextActions.showText(open.getExtendedDescription(sender).get()))
                                    .build(),

                            Text.of(prefix, TextColors.GREEN, "list [<user>]")
                                    .toBuilder()
                                    .onHover(TextActions.showText(list.getExtendedDescription(sender).get()))
                                    .build(),

                            Text.of(prefix, TextColors.GREEN, "close <player>")
                                    .toBuilder()
                                    .onHover(TextActions.showText(close.getExtendedDescription(sender).get()))
                                    .build(),

                            Text.of(prefix, TextColors.GREEN, "delete <backpack name> <drop items (true/false)> [<user>]")
                                    .toBuilder()
                                    .onHover(TextActions.showText(create.getExtendedDescription(sender).get()))
                                    .build()
                    );
                    PaginationList.builder()
                            .title(Text.of(TextColors.BLUE, "SpongyBackpacks Commands"))
                            .header(Text.of(TextColors.YELLOW, "Hover over the commands to check their description"))
                            .padding(Text.of(TextStyles.STRIKETHROUGH, "-"))
                            .contents(text)
                            .sendTo(sender);
                    return CommandResult.success();
                })
                .child(create, "create", "c")
                .child(open, "open", "o")
                .child(close, "close")
                .child(delete, "delete", "d")
                .child(list, "list", "l")
                .build();

        Sponge.getCommandManager().register(SpongyBackpacks.getInstance(), backpack, "backpacks", "spongybackpacks", "sb", "bp", "sbp");
    }

}
