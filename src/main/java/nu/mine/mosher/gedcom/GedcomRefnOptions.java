package nu.mine.mosher.gedcom;

@SuppressWarnings({"access", "WeakerAccess", "unused"})
public class GedcomRefnOptions extends GedcomOptions {
    public void help() {
        this.help = true;
        System.err.println("Usage: gedcom-refn [OPTIONS] <in.ged >out.ged");
        System.err.println("Adds REFN (UUIDs) to top-level GEDCOM records that don't already one.");
        System.err.println("Options:");
        options();
    }

    public GedcomRefnOptions verify() {
        if (this.help) {
            return this;
        }
        return this;
    }
}
