//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acme;

import wt.fc.EnumeratedType;
import wt.util.WTInvalidParameterException;

import java.util.Hashtable;
import java.util.Locale;

public abstract class _PetKind extends EnumeratedType {
    static final long serialVersionUID = 1L;
    static final String RESOURCE = "com.acme.acmeResource";
    static final String CLASSNAME = (new PetKind()).getClass().getName();
    static final String CLASS_RESOURCE = "com.acme.PetKindRB";
    static Hashtable localeSets;
    private static volatile EnumeratedType[] valueSet;

    public _PetKind() {
    }

    static EnumeratedType[] _valueSet() {
        if (valueSet == null) {
            Class var0 = _PetKind.class;
            synchronized(_PetKind.class) {
                try {
                    if (valueSet == null) {
                        valueSet = initializeLocaleSet((Locale)null);
                    }
                } catch (Throwable var3) {
                    throw new ExceptionInInitializerError(var3);
                }
            }
        }

        return valueSet;
    }

    public static PetKind newPetKind(int secretHandshake) throws IllegalAccessException {
        validateFriendship(secretHandshake);
        return new PetKind();
    }

    public static PetKind toPetKind(String internal_value) throws WTInvalidParameterException {
        return (PetKind)toEnumeratedType(internal_value, _valueSet());
    }

    public static PetKind getPetKindDefault() {
        return (PetKind)defaultEnumeratedType(_valueSet());
    }

    public static PetKind[] getPetKindSet() {
        PetKind[] set = new PetKind[_valueSet().length];
        System.arraycopy(valueSet, 0, set, 0, valueSet.length);
        return set;
    }

    public EnumeratedType[] getValueSet() {
        return getPetKindSet();
    }

    protected EnumeratedType[] valueSet() {
        return _valueSet();
    }

    protected EnumeratedType[] getLocaleSet(Locale locale) {
        EnumeratedType[] request = null;
        if (localeSets == null) {
            localeSets = new Hashtable();
        } else {
            request = (EnumeratedType[])((EnumeratedType[])localeSets.get(locale));
        }

        if (request == null) {
            try {
                request = initializeLocaleSet(locale);
            } catch (Throwable var4) {
                ;
            }

            localeSets.put(locale, request);
        }

        return request;
    }

    static EnumeratedType[] initializeLocaleSet(Locale locale) throws Throwable {
        return instantiateSet(PetKind.class.getMethod("newPetKind", Integer.TYPE), "com.acme.PetKindRB", locale);
    }
}
