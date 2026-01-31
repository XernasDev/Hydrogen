package dev.xernas.hydrogen;

import dev.xernas.photon.exceptions.PhotonException;

public class HydrogenException extends PhotonException {

    public HydrogenException(String message) {
        super(message);
    }

    public HydrogenException(String message, Throwable cause) {
        super(message, cause);
    }

    public HydrogenException(Throwable cause) {
        super(cause);
    }


}
