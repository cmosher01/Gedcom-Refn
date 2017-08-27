package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import nu.mine.mosher.mopper.ArgParser;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

import static nu.mine.mosher.logging.Jul.log;

/**
 * For each top-level record (INDI, FAM, SOUR, etc.) that does not have a REFN,
 * add one with a UUID for a value.
 *
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
//        addNodes(createNewRefns(tree.getRoot(), new ArrayList<>(4096)));
        createNewRefns(tree.getRoot());
        return true;
    }



    private static class ChildToBeAdded {
        TreeNode<GedcomLine> parent;
        TreeNode<GedcomLine> child;
        ChildToBeAdded(TreeNode<GedcomLine> parent, TreeNode<GedcomLine> child) {
            this.parent = parent;
            this.child = child;
        }
    }

    private void /*List<ChildToBeAdded>*/ createNewRefns(final TreeNode<GedcomLine> root/*, final List<ChildToBeAdded> refns*/) {
        root.forEach(top -> {
            final GedcomLine gedcomLine = top.getObject();
            if (gedcomLine != null && gedcomLine.hasID()) {
                if (!hasRefn(top)) {
//                    refns.add(new ChildToBeAdded(top, new TreeNode<>(gedcomLine.createChild(GedcomTag.REFN, UUID.randomUUID().toString()))));
                    top.addChild(new TreeNode<>(gedcomLine.createChild(GedcomTag.REFN, UUID.randomUUID().toString())));
                }
            }
        });
//        return refns;
    }



//    private static void addNodes(final List<ChildToBeAdded> adds) {
//        adds.forEach(add -> {
//            add.parent.addChild(add.child);
//        });
//    }






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
