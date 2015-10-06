package a2.commands;

/**
 * Created by willk on 10/5/2015.
 */
public class GLVersion {
    private static GLVersion instance = null;
    private String version;

    private GLVersion() {
        version = null;
    }

    public static GLVersion getInstance() {
        if (instance == null) instance = new GLVersion();
        return instance;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String s) {
        this.version = s;
    }
}
