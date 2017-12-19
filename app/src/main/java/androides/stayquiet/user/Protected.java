package androides.stayquiet.user;

/**
 * Created by developer on 10/12/17.
 */

public class Protected {
    private String username;
    private Boolean canWatch;

    public Protected() {
        this("", false);
    }

    public Protected(String username, Boolean canWatch) {
        setUsername(username);
        setCanWatch(canWatch);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Protected auxProtected = (Protected) object;
            if (getUsername().equals(auxProtected.getUsername())) {
                result = true;
            }
        }

        return result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getCanWatch() {
        return canWatch;
    }

    public void setCanWatch(Boolean canWatch) {
        this.canWatch = canWatch;
    }
}
