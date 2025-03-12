package mode;

import java.util.Scanner;

import eggo.OutputHandler;
import eggo.Helper;

import mode.task.TaskManager;

/**
 * This class
 * - Allows the user to add tasks, list them, rename and delete.
 * - Allows the tasks to be marked as done, urgent or important.
 * - Task includes "todo", "deadline" and "event" type.
 * - The list will be saved upon updates and automatically load upon entry.
 */
public class TaskMode implements Mode {
    public static final String WELCOME_MESSAGE = """
            Welcome to Task Mode!
            Type 'help' to view the help menu.
            Type 'exit' to return to the Lobby.""";
    public static final String HELP_MESSAGE = """
            Type 'list' to view your tasks.
            Type 'todo [description]' to add a to-do.
            Type 'deadline [description] /by [time]' to add a deadline.
            Type 'event [description] /from [start time] /to [end time]' to add an event.
            Type 'mark [indices]' to mark tasks as done.
            Type 'unmark [indices]' to mark tasks as not done.
            Type 'urg [indices]' to mark tasks as urgent.
            Type 'noturg [indices]' to remove urgent mark.
            Type 'imp [indices]' to mark tasks as important.
            Type 'notimp [indices]' to remove important mark.
            Type 'find [keyword]' or 'find /type [todo|deadline|event]' to find tasks).
            Type 'rename [index] [new task name]' to rename a task).
            Type 'delete [indices]' to remove tasks.
            Type 'exit' to return to the Lobby.""";

    @Override
    public void start(Scanner scanner) {
        OutputHandler.printInfo(WELCOME_MESSAGE);

        while (true) {
            try {
                String input = scanner.nextLine().trim();
                String[] parts = input.split(" ", 2); // To be examined
                String command = parts[0].toLowerCase();
                String arguments = parts.length > 1 ? parts[1].trim() : "";

                switch (command) {
                    case "exit" -> {
                        OutputHandler.printInfo("Exiting Task Mode.");
                        return;
                    }
                    case "todo" -> TaskManager.addTodo(arguments);
                    case "deadline" -> TaskManager.addDeadline(arguments);
                    case "event" -> TaskManager.addEvent(arguments);
                    case "list" -> TaskManager.listTasks();
                    case "mark" -> TaskManager.updateTasksDone(arguments, true);
                    case "unmark" -> TaskManager.updateTasksDone(arguments, false);
                    case "urg" -> TaskManager.updateTaskUrgency(arguments, true, "marked as urgent");
                    case "noturg" -> TaskManager.updateTaskUrgency(arguments, false, "removed urgent mark");
                    case "imp" -> TaskManager.updateTaskImportance(arguments, true, "marked as important");
                    case "notimp" -> TaskManager.updateTaskImportance(arguments, false, "removed important mark");
                    case "find" -> TaskManager.findTask(arguments);
                    case "rename" -> TaskManager.renameTask(arguments);
                    case "delete" -> TaskManager.deleteTasks(arguments);
                    case "help" -> HelpHandler.help();
                    default -> OutputHandler.printWarning("Unknown command: " + command);
                }
            } catch (Exception e) {
                OutputHandler.printError(e.getMessage());
            }
        }
    }

    private static class HelpHandler implements Helper {
        public static final String DELETE_DETAIL = "\n\n\"delete [description]\" can only remove one task at a time.";

        public static void help() {
            OutputHandler.print(HELP_MESSAGE + DELETE_DETAIL);
        }
    }
}
