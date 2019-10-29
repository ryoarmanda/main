package seedu.address.ui.textfield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.Prefix;

public class AutofillSupportedCommand {
    private Map<Prefix, Boolean> requiredPrefixToIsPresentMapping;
    private Map<Prefix, Boolean> optionalPrefixToIsPresentMapping;
    private String command;

    AutofillSupportedCommand(String commandWord, List<Prefix> requiredPrefixes, List<Prefix> optionalPrefixes) {
        this.command = commandWord;

        requiredPrefixToIsPresentMapping = new HashMap<>();
        for (Prefix p : requiredPrefixes) {
            requiredPrefixToIsPresentMapping.put(p, false);
        }

        optionalPrefixToIsPresentMapping = new HashMap<>();
        for (Prefix p : optionalPrefixes) {
            optionalPrefixToIsPresentMapping.put(p, false);
        }
    }

    public String getCommand() {
        return command;
    }

    /**
     * Returns a list of missing prefixes to add to the auto completion suggestions.
     * @param input The string to check for prefixes.
     */
    public List<Prefix> getMissingPrefixes(String input) {
        ArrayList<Prefix> prefixes = new ArrayList<>(requiredPrefixToIsPresentMapping.keySet());
        prefixes.addAll(optionalPrefixToIsPresentMapping.keySet());
        Prefix[] prefixArray = prefixes.toArray(new Prefix[]{});

        // tokenize to find prefixes
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(input, prefixArray);

        ArrayList<Prefix> remaining = new ArrayList<>();

        // find missing required prefixes
        requiredPrefixToIsPresentMapping.forEach((p, v) -> {
            if (argumentMultimap.getValue(p).isEmpty()) {
                remaining.add(p);
            }
        });

        // find missing optional prefixes
        optionalPrefixToIsPresentMapping.forEach((p, v) -> {
            if (argumentMultimap.getValue(p).isEmpty()) {
                remaining.add(p);
            }
        });
        return remaining;
    }
}