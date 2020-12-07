//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acme;

import java.beans.PropertyChangeEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import wt.content.ContentHolder;
import wt.content.HttpContentOperation;
import wt.fc.EnumeratedTypeUtil;
import wt.fc.EvolvableHelper;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.introspection.ClassInfo;
import wt.introspection.PropertyDisplayName;
import wt.introspection.WTIntrospectionException;
import wt.introspection.WTIntrospector;
import wt.ownership.Ownable;
import wt.ownership.Ownership;
import wt.pds.PDSObjectInput;
import wt.pds.PDSObjectOutput;
import wt.pds.PersistentRetrieveIfc;
import wt.pds.PersistentStoreIfc;
import wt.pom.DatastoreException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.util.WTStringUtilities;

public abstract class _Pet extends WTObject implements ContentHolder, Ownable, Externalizable {
    static final long serialVersionUID = 1L;
    static final String RESOURCE = "com.acme.acmeResource";
    static final String CLASSNAME = Pet.class.getName();
    public static final String NAME = "name";
    static int NAME_UPPER_LIMIT = -1;
    String name;
    public static final String KIND = "kind";
    static int KIND_UPPER_LIMIT = -1;
    PetKind kind;
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    Timestamp dateOfBirth;
    public static final String FIXED = "fixed";
    boolean fixed;
    Vector contentVector;
    boolean hasContents;
    HttpContentOperation operation;
    Vector httpVector;
    Ownership ownership;
    public static final long EXTERNALIZATION_VERSION_UID = -670864556025876334L;

    public _Pet() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) throws WTPropertyVetoException {
        this.nameValidate(name);
        this.name = name;
    }

    void nameValidate(String name) throws WTPropertyVetoException {
        if (name != null && name.trim().length() != 0) {
            if (NAME_UPPER_LIMIT < 1) {
                try {
                    NAME_UPPER_LIMIT = (Integer)WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("name").getValue("UpperLimit");
                } catch (WTIntrospectionException var3) {
                    NAME_UPPER_LIMIT = 60;
                }
            }

            if (name != null && !PersistenceHelper.checkStoredLength(name.toString(), NAME_UPPER_LIMIT, true)) {
                throw new WTPropertyVetoException("wt.introspection.introspectionResource", "20", new Object[]{new PropertyDisplayName(CLASSNAME, "name"), String.valueOf(Math.min(NAME_UPPER_LIMIT, PersistenceHelper.DB_MAX_SQL_STRING_SIZE / PersistenceHelper.DB_MAX_BYTES_PER_CHAR))}, new PropertyChangeEvent(this, "name", this.name, name));
            }
        } else {
            throw new WTPropertyVetoException("wt.fc.fcResource", "22", new Object[]{new PropertyDisplayName(CLASSNAME, "name")}, new PropertyChangeEvent(this, "name", this.name, name));
        }
    }

    public PetKind getKind() {
        return this.kind;
    }

    public void setKind(PetKind kind) throws WTPropertyVetoException {
        this.kindValidate(kind);
        this.kind = kind;
    }

    void kindValidate(PetKind kind) throws WTPropertyVetoException {
        if (KIND_UPPER_LIMIT < 1) {
            try {
                KIND_UPPER_LIMIT = (Integer)WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("kind").getValue("UpperLimit");
            } catch (WTIntrospectionException var3) {
                KIND_UPPER_LIMIT = 40;
            }
        }

        if (kind != null && !PersistenceHelper.checkStoredLength(kind.toString(), KIND_UPPER_LIMIT, true)) {
            throw new WTPropertyVetoException("wt.introspection.introspectionResource", "20", new Object[]{new PropertyDisplayName(CLASSNAME, "kind"), String.valueOf(Math.min(KIND_UPPER_LIMIT, PersistenceHelper.DB_MAX_SQL_STRING_SIZE / PersistenceHelper.DB_MAX_BYTES_PER_CHAR))}, new PropertyChangeEvent(this, "kind", this.kind, kind));
        }
    }

    public Timestamp getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Timestamp dateOfBirth) throws WTPropertyVetoException {
        this.dateOfBirthValidate(dateOfBirth);
        this.dateOfBirth = dateOfBirth;
    }

    void dateOfBirthValidate(Timestamp dateOfBirth) throws WTPropertyVetoException {
    }

    public boolean isFixed() {
        return this.fixed;
    }

    public void setFixed(boolean fixed) throws WTPropertyVetoException {
        this.fixedValidate(fixed);
        this.fixed = fixed;
    }

    void fixedValidate(boolean fixed) throws WTPropertyVetoException {
    }

    public Vector getContentVector() {
        return this.contentVector;
    }

    public void setContentVector(Vector contentVector) throws WTPropertyVetoException {
        this.contentVectorValidate(contentVector);
        this.contentVector = contentVector;
    }

    void contentVectorValidate(Vector contentVector) throws WTPropertyVetoException {
    }

    public boolean isHasContents() {
        return this.hasContents;
    }

    public void setHasContents(boolean hasContents) throws WTPropertyVetoException {
        this.hasContentsValidate(hasContents);
        this.hasContents = hasContents;
    }

    void hasContentsValidate(boolean hasContents) throws WTPropertyVetoException {
    }

    public HttpContentOperation getOperation() {
        return this.operation;
    }

    public void setOperation(HttpContentOperation operation) throws WTPropertyVetoException {
        this.operationValidate(operation);
        this.operation = operation;
    }

    void operationValidate(HttpContentOperation operation) throws WTPropertyVetoException {
    }

    public Vector getHttpVector() {
        return this.httpVector;
    }

    public void setHttpVector(Vector httpVector) throws WTPropertyVetoException {
        this.httpVectorValidate(httpVector);
        this.httpVector = httpVector;
    }

    void httpVectorValidate(Vector httpVector) throws WTPropertyVetoException {
    }

    public Ownership getOwnership() {
        return this.ownership;
    }

    public void setOwnership(Ownership ownership) {
        this.ownership = ownership;
    }

    public String getConceptualClassname() {
        return CLASSNAME;
    }

    public ClassInfo getClassInfo() throws WTIntrospectionException {
        return WTIntrospector.getClassInfo(this.getConceptualClassname());
    }

    public String getType() {
        try {
            return this.getClassInfo().getDisplayName();
        } catch (WTIntrospectionException var2) {
            return WTStringUtilities.tail(this.getConceptualClassname(), '.');
        }
    }

    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeLong(-670864556025876334L);
        super.writeExternal(output);
        output.writeObject(this.dateOfBirth);
        output.writeBoolean(this.fixed);
        output.writeObject(this.kind == null ? null : this.kind.getStringValue());
        output.writeObject(this.name);
        output.writeObject(this.ownership);
        if (!(output instanceof PDSObjectOutput)) {
            output.writeObject(this.contentVector);
            output.writeBoolean(this.hasContents);
            output.writeObject(this.httpVector);
            output.writeObject(this.operation);
        }

    }

    protected void super_writeExternal_Pet(ObjectOutput output) throws IOException {
        super.writeExternal(output);
    }

    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        long readSerialVersionUID = input.readLong();
        this.readVersion((Pet)this, input, readSerialVersionUID, false, false);
    }

    protected void super_readExternal_Pet(ObjectInput input) throws IOException, ClassNotFoundException {
        super.readExternal(input);
    }

    public void writeExternal(PersistentStoreIfc output) throws SQLException, DatastoreException {
        super.writeExternal(output);
        output.setTimestamp("dateOfBirth", this.dateOfBirth);
        output.setBoolean("fixed", this.fixed);
        output.setString("kind", this.kind == null ? null : this.kind.toString());
        output.setString("name", this.name);
        output.writeObject("ownership", this.ownership, Ownership.class, true);
    }

    public void readExternal(PersistentRetrieveIfc input) throws SQLException, DatastoreException {
        super.readExternal(input);
        this.dateOfBirth = input.getTimestamp("dateOfBirth");
        this.fixed = input.getBoolean("fixed");
        String kind_string_value = input.getString("kind");
        if (kind_string_value != null) {
            this.kind = (PetKind)ClassInfo.getConstrainedEnum(this.getClass(), "kind", kind_string_value);
            if (this.kind == null) {
                this.kind = PetKind.toPetKind(kind_string_value);
            }
        }

        this.name = input.getString("name");
        this.ownership = (Ownership)input.readObject("ownership", this.ownership, Ownership.class, true);
    }

    boolean readVersion_670864556025876334L(ObjectInput input, long readSerialVersionUID, boolean superDone) throws IOException, ClassNotFoundException {
        if (!superDone) {
            super.readExternal(input);
        }

        this.dateOfBirth = (Timestamp)input.readObject();
        this.fixed = input.readBoolean();
        String kind_string_value = (String)input.readObject();

        try {
            this.kind = (PetKind)EnumeratedTypeUtil.toEnumeratedType(kind_string_value);
        } catch (WTInvalidParameterException var7) {
            this.kind = PetKind.toPetKind(kind_string_value);
        }

        this.name = (String)input.readObject();
        this.ownership = (Ownership)input.readObject();
        if (!(input instanceof PDSObjectInput)) {
            this.contentVector = (Vector)input.readObject();
            this.hasContents = input.readBoolean();
            this.httpVector = (Vector)input.readObject();
            this.operation = (HttpContentOperation)input.readObject();
        }

        return true;
    }

    protected boolean readVersion(Pet thisObject, ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone) throws IOException, ClassNotFoundException {
        boolean success = true;
        if (readSerialVersionUID == -670864556025876334L) {
            return this.readVersion_670864556025876334L(input, readSerialVersionUID, superDone);
        } else {
            success = this.readOldVersion(input, readSerialVersionUID, passThrough, superDone);
            if (input instanceof PDSObjectInput) {
                EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();
            }

            return success;
        }
    }

    protected boolean super_readVersion_Pet(_Pet thisObject, ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone) throws IOException, ClassNotFoundException {
        return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
    }

    boolean readOldVersion(ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone) throws IOException, ClassNotFoundException {
        throw new InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID=" + readSerialVersionUID + " local class externalizationVersionUID=" + -670864556025876334L);
    }
}
