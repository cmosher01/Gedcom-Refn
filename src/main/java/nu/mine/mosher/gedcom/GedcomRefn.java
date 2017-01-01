package nu.mine.mosher.gedcom;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.exception.InvalidLevel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * For each top-level record (INDI, FAM, SOUR, etc.) that does not have a REFN,
 * add one with a UUID for a value.
 *
 * Created by user on 12/12/16.
 */
public class GedcomRefn {
    private final File file;
    private Charset charset;
    private GedcomTree gt;
    private final Map<String, String> mapRemapIds = new HashMap<>(4096);



    public static void main(final String... args) throws InvalidLevel, IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("usage: java -jar gedcom-refn in.ged >out.ged");
        } else {
            new GedcomRefn(args[0]).main();
        }
    }



    GedcomRefn(final String filename) {
        this.file = new File(filename);
    }

    public void main() throws IOException, InvalidLevel {
        loadGedcom();
        updateGedcom();
        saveGedcom();
    }

    private void loadGedcom() throws IOException, InvalidLevel {
        this.charset = Gedcom.getCharset(this.file);
        this.gt = Gedcom.parseFile(file, this.charset, false);
    }

    private void updateGedcom() {
        addNodes(createNewRefns(this.gt.getRoot(), new ArrayList<>(4096)));
    }

    private void saveGedcom() throws IOException {
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), this.charset));

        Gedcom.writeFile(this.gt, out, Integer.MAX_VALUE);

        out.flush();
        out.close();
    }


    private static class ChildToBeAdded {
        TreeNode<GedcomLine> parent;
        TreeNode<GedcomLine> child;
        ChildToBeAdded(TreeNode<GedcomLine> parent, TreeNode<GedcomLine> child) {
            this.parent = parent;
            this.child = child;
        }
    }

    private List<ChildToBeAdded> createNewRefns(final TreeNode<GedcomLine> root, final List<ChildToBeAdded> refns) {
        root.forEach(top -> {
            final GedcomLine gedcomLine = top.getObject();
            if (gedcomLine != null && gedcomLine.hasID()) {
                if (!hasRefn(top)) {
                    final TreeNode<GedcomLine> refn = new TreeNode<>(new GedcomLine(gedcomLine.getLevel() + 1, "", GedcomTag.REFN.name(), UUID.randomUUID().toString()));
                    refns.add(new ChildToBeAdded(top, refn));
                }
            }
        });
        return refns;
    }



    private static void addNodes(final List<ChildToBeAdded> adds) {
        adds.forEach(add -> {
            add.parent.addChild(add.child);
        });
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
