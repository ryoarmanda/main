package seedu.moolah.logic.parser.statistics;

import static seedu.moolah.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.moolah.commons.core.Messages.MESSAGE_REPEATED_PREFIX_COMMAND;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.moolah.logic.parser.CliSyntax.PREFIX_START_DATE;

import java.util.Collections;
import java.util.List;

import seedu.moolah.commons.core.Messages;
import seedu.moolah.logic.commands.statistics.StatsCommand;
import seedu.moolah.logic.commands.statistics.StatsDescriptor;
import seedu.moolah.logic.parser.ArgumentMultimap;
import seedu.moolah.logic.parser.ArgumentTokenizer;
import seedu.moolah.logic.parser.Parser;
import seedu.moolah.logic.parser.ParserUtil;
import seedu.moolah.logic.parser.Prefix;
import seedu.moolah.logic.parser.exceptions.ParseException;
import seedu.moolah.model.expense.Timestamp;

/**
 * Parses input arguments and creates a new StatsCommand object
 */
public class StatsCommandParser implements Parser<StatsCommand> {


    public static final List<Prefix> REQUIRED_PREFIXES = Collections.unmodifiableList(List.of());

    public static final List<Prefix> OPTIONAL_PREFIXES = Collections.unmodifiableList(List.of(
            PREFIX_START_DATE, PREFIX_END_DATE));

    /**
     * Parses the given {@code String} of arguments in the context of the StatsCommand
     * and returns an StatsCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public StatsCommand parse(String args) throws ParseException {

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_START_DATE, PREFIX_END_DATE);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatsCommand.MESSAGE_USAGE));
        }
        if (argMultimap.hasRepeatedPrefixes(PREFIX_START_DATE, PREFIX_END_DATE)) {
            throw new ParseException(MESSAGE_REPEATED_PREFIX_COMMAND);
        }

        StatsDescriptor statsDescriptor = new StatsDescriptor();

        Timestamp startDate = null;
        Timestamp endDate = null;

        boolean isStartPresent = argMultimap.getValue(PREFIX_START_DATE).isPresent();
        boolean isEndPresent = argMultimap.getValue(PREFIX_END_DATE).isPresent();
        if (isStartPresent && isEndPresent) {
            checkStartBeforeEnd(argMultimap);
            startDate = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_START_DATE).get());
            endDate = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_END_DATE).get());
            statsDescriptor.setStartDate(startDate);
            statsDescriptor.setEndDate(endDate);
        } else if (isStartPresent) {
            startDate = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_START_DATE).get());
            statsDescriptor.setStartDate(startDate);
        } else if (isEndPresent) {
            endDate = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_END_DATE).get());
            statsDescriptor.setEndDate(endDate);
        }

        return new StatsCommand(statsDescriptor);
    }

    /**
     * Parses the given {@code String} of arguments in the context of the StatsCommand
     * Checks that start date is before the end date of the given {@code ArgumentMultimap}
     *
     * @throws ParseException if the detected start date is after the end date
     */
    private void checkStartBeforeEnd(ArgumentMultimap argMultimap) throws ParseException {
        Timestamp startDate = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_START_DATE).get());
        Timestamp endDate = ParserUtil.parseTimestamp(argMultimap.getValue(PREFIX_END_DATE).get());
        if (endDate.isBefore(startDate)) {
            throw new ParseException(Messages.MESSAGE_CONSTRAINTS_END_DATE);
        }
    }

}
