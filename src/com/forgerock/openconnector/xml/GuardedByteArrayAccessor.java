package com.forgerock.openconnector.xml;

import java.util.Arrays;
import org.identityconnectors.common.security.GuardedByteArray;

/**
 * Sample Class Doc
 *
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */

public class GuardedByteArrayAccessor implements GuardedByteArray.Accessor {

    public static final String code_id = "$Id$";
    private byte[] array;
    
    public void access(byte[] clearBytes) {
        array = new byte[clearBytes.length];
        System.arraycopy(clearBytes, 0, array, 0, array.length);
    }

    public byte[] getArray() {
        return array;
    }

    public void clear() {
        Arrays.fill(array, Byte.MIN_VALUE);
    }
}
