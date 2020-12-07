package com.acme;
import wt.fc.Persistable;
import wt.fc.collections.WTCollection;
import wt.util.WTException;import wt.verification.Verifier;
public class PetVerifier implements Verifier {
    @Override
    public boolean verify(Persistable a_object) throws WTException {
        return true;
    }
    @Override
    public boolean verify(WTCollection a_objects) throws WTException {
        return true;
    }}