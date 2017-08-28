package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.IOException;
import java.util.UUID;

import static nu.mine.mosher.logging.Jul.log;

/**
 * For each top-level record (INDI, FAM, SOUR, etc.) that does not have a REFN,
 * add one with a UUID for a value.
 * <p>
 * Created by user on 12/12/16.
 */
public class GedcomRefn implements Gedcom.Processor {
    private final GedcomRefnOptions options;



    public static void main(final String... args) throws InvalidLevel, IOException {
        log();
        final GedcomRefnOptions options = new ArgParser<>(new GedcomRefnOptions()).parse(args).verify();
        new Gedcom(options, new GedcomRefn(options)).main();
        System.out.flush();
        System.err.flush();
    }



    private GedcomRefn(final GedcomRefnOptions options) {
        this.options = options;
    }

    @Override
    public boolean process(final GedcomTree tree) {
        tree.getRoot().forEach(top -> {
            final GedcomLine gedcomLine = top.getObject();
            if (gedcomLine != null && gedcomLine.hasID()) {
                if (!hasRefn(top)) {
                    top.addChild(new TreeNode<>(gedcomLine.createChild(GedcomTag.REFN, UUID.randomUUID().toString())));
                }
            }
        });
        return true;
    }



    private static boolean hasRefn(final TreeNode<GedcomLine> top) {
        for (final TreeNode<GedcomLine> attr : top) {
            final GedcomLine gedcomLine = attr.getObject();
            if (gedcomLine != null && gedcomLine.getTag().equals(GedcomTag.REFN)) {
                return true;
            }
        }
        return false;
    }
}
