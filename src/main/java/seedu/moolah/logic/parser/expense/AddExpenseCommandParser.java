package seedu.moolah.logic.parser.expense;

import static seedu.moolah.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.moolah.commons.core.Messages.MESSAGE_REPEATED_PREFIX_COMMAND;
import static seedu.moolah.logic.commands.expense.AddExpenseCommand.MESSAGE_USAGE;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_MENU;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_TIMESTAMP;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import seedu.moolah.logic.commands.expense.AddExpenseCommand;
import seedu.moolah.logic.parser.ArgumentMultimap;
import seedu.moolah.logic.parser.ArgumentTokenizer;
import seedu.moolah.logic.parser.Parser;
import seedu.moolah.logic.parser.ParserUtil;
import seedu.moolah.logic.parser.Prefix;
import seedu.moolah.logic.parser.exceptions.ParseException;
import seedu.moolah.model.Timekeeper;
import seedu.moolah.model.expense.Category;
import seedu.moolah.model.expense.Description;
import seedu.moolah.model.expense.Expense;
import seedu.moolah.model.expense.Price;
import seedu.moolah.model.expense.Timestamp;
import seedu.moolah.model.expense.util.UniqueIdentifierGenerator;
import seedu.moolah.model.menu.MenuItem;

/**
 * Parses input arguments and creates a new AddExpenseCommand object
 */
public class AddExpenseCommandParser implements Parser<AddExpenseCommand> {

    public static final List<Prefix> REQUIRED_PREFIXES = Collections.unmodifiableList(List.of(
            PREFIX_DESCRIPTION, PREFIX_PRICE, PREFIX_CATEGORY, PREFIX_MENU
    ));

    public static final List<Prefix> OPTIONAL_PREFIXES = Collections.unmodifiableList(List.of(
            PREFIX_TIMESTAMP
    ));

    /**
     * Parses the given {@code String} of arguments in the context of the AddExpenseCommand
     * and returns an AddExpenseCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddExpenseCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_DESCRIPTION, PREFIX_PRICE, PREFIX_CATEGORY, PREFIX_MENU,
                        PREFIX_TIMESTAMP);

        boolean hasMenuItemFormat = arePrefixesPresent(argMultimap, PREFIX_MENU)
                && argMultimap.getPreamble().isEmpty();
        boolean hasUserInputFormat = arePrefixesPresent(argMultimap, PREFIX_DESCRIPTION, PREFIX_PRICE, PREFIX_CATEGORY)
                && argMultimap.getPreamble().isEmpty();

        // Checks whether both or neither formats are present
        if (hasUserInputFormat == hasMenuItemFormat) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        if (hasUserInputFormat) {
            return parseUserInputExpense(argMultimap);
        } else {
            return parseMenuItemExpense(argMultimap);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if none of the prefixes are repeated
     * {@code ArgumentMultimap}.
     */
    private static boolean hasRepeatedPrefixes(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return !(Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getAllValues(prefix).size() <= 1));
    }

    /**
     * Parses the tokenized input according to the "user-input expense" format
     * @param argMultimap The tokenized input
     * @return an {@AddExpenseCommand} object for execution
     * @throws ParseException if the user input does not conform the format
     */
    private static AddExpenseCommand parseUserInputExpense(ArgumentMultimap argMultimap) throws ParseException {
        if (hasRepeatedPrefixes(argMultimap, PREFIX_DESCRIPTION, PREFIX_PRICE, PREFIX_CATEGORY, PREFIX_TIMESTAMP)) {
            throw new ParseException(MESSAGE_REPEATED_PREFIX_COMMAND);
        }

        Description description = ParserUtil.parseDescription(argMultimap.getValue(PREFIX_DESCRIPTION).get());
        Price price = ParserUtil.parsePrice(argMultimap.getValue(PREFIX_PRICE).get());
        Category category = ParserUtil.parseCategory(argMultimap.getValue(PREFIX_CATEGORY).get());
        boolean isTimestampPresent = argMultimap.getValue(PREFIX_TIMESTAMP).isPresent();

        if (isTimestampPresent) {
            Timestamp timestamp = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_TIMESTAMP).get());
            if (Timekeeper.isFutureTimestamp(timestamp)) {
                throw new ParseException("Expense cannot be in the future");
            }
            Expense expense = new Expense(description, price, category, timestamp,
                    UniqueIdentifierGenerator.generateRandomUniqueIdentifier());
            return new AddExpenseCommand(expense);

        } else {
            Expense expense = new Expense(description, price, category,
                    UniqueIdentifierGenerator.generateRandomUniqueIdentifier());
            return new AddExpenseCommand(expense);
        }
    }

    /**
     * Parses the tokenized input according to the "menu item expense" format
     * @param argMultimap The tokenized input
     * @return an {@AddExpenseCommand} object for execution
     * @throws ParseException if the user input does not conform the format
     */
    private static AddExpenseCommand parseMenuItemExpense(ArgumentMultimap argMultimap) throws ParseException {
        if (hasRepeatedPrefixes(argMultimap, PREFIX_MENU, PREFIX_TIMESTAMP)) {
            throw new ParseException(MESSAGE_REPEATED_PREFIX_COMMAND);
        }

        MenuItem menuItem = ParserUtil.parseMenuItem(argMultimap.getValue(PREFIX_MENU).get());
        boolean isTimestampPresent = argMultimap.getValue(PREFIX_TIMESTAMP).isPresent();

        if (isTimestampPresent) {
            Timestamp timestamp = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_TIMESTAMP).get());
            if (Timekeeper.isFutureTimestamp(timestamp)) {
                throw new ParseException("Expense cannot be in the future");
            }
            Expense expense = new Expense(menuItem, timestamp,
                    UniqueIdentifierGenerator.generateRandomUniqueIdentifier());
            return new AddExpenseCommand(expense);
        } else {
            Expense expense = new Expense(menuItem,
                    UniqueIdentifierGenerator.generateRandomUniqueIdentifier());
            return new AddExpenseCommand(expense);
        }
    }
}
