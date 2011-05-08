package org.forgerock.openicf.usd.resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;

import com.statoil.cims.common.Pair;
import com.statoil.cims.connector.ICIMSConnection;
import org.forgerock.openicf.usd.meta.MetaResource;
import com.statoil.cims.identityconnector.IConnectorService;
import com.statoil.cims.model.ActiveDirectoryRole;
import com.statoil.cims.model.BPCRole;
import com.statoil.cims.model.CRBARole;
import com.statoil.cims.model.Conflict;
import com.statoil.cims.model.ConflictDetails;
import com.statoil.cims.model.DISARole;
import com.statoil.cims.model.MitigationControl;
import com.statoil.cims.model.MitigationControlDetails;
import com.statoil.cims.model.PIMSRole;
import com.statoil.cims.model.Person;
import com.statoil.cims.model.PersonDetails;
import com.statoil.cims.model.Risk;
import com.statoil.cims.model.RiskDetails;
import com.statoil.cims.model.RoleType;
import com.statoil.cims.model.SAPRole;
import com.statoil.cims.model.VALEORole;

public class ResourceFactory {

    static final String CANNOT_CREATE = "Cannot create entity.";
    static final String NOT_ACCESSIBLE = CANNOT_CREATE + " Method '%s' is not accessible";
    static final String NOT_IMPLEMENTED = CANNOT_CREATE + " Method '%s' has not been implemented.";

    public UserResource createPerson(ICIMSConnection connection, String identifier) throws AlreadyExistsException {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        String query = "person.findBeforeCreate";
        Object result = service.findNamedObject(query, new Pair<String, Object>("objectName", identifier));
        if (result != null && ((Long) result) > 0) {
            throw new AlreadyExistsException(identifier);
        }

        Person newPerson = new Person();
        newPerson.setShortName(identifier);
        newPerson.setIsDeleted(Boolean.FALSE);
        newPerson.setStatus(0);

        PersonDetails details = new PersonDetails();
        details.setMainPerson(newPerson);
//		newPerson.getPersonChangeHistory().add(details);
        newPerson.setLastObjectDetails(details);
        return new UserResource(newPerson, connection);
    }

    public SAPRoleResource createSAPRole(ICIMSConnection connection, String identifier) throws AlreadyExistsException {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists("role.sap.exists", new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(2));
        SAPRole newRole = new SAPRole();
        newRole.setRoleType(type);
        return new SAPRoleResource(newRole, connection);
    }

    public PIMSRoleResource createPIMSRole(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists("role.pims.exists", new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(3));
        PIMSRole newRole = new PIMSRole();
        newRole.setRoleType(type);
        return new PIMSRoleResource(newRole, connection);
    }

    public VALEORoleResource createVALEORole(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists("role.VALEO.exists", new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(3));
        VALEORole newRole = new VALEORole();
        newRole.setRoleType(type);
        return new VALEORoleResource(newRole, connection);
    }

    public ADRoleResource createActiveDirectoryRole(ICIMSConnection connection, String identifier) throws AlreadyExistsException {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists("role.ad.exists", new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(4));
        ActiveDirectoryRole newRole = new ActiveDirectoryRole();
        newRole.setRoleType(type);
        return new ADRoleResource(newRole, connection);
    }

    public CRBARoleResource createCRBARole(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists("role.crba.exists", new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(5));
        CRBARole newRole = new CRBARole();
        newRole.setRoleType(type);
        return new CRBARoleResource(newRole, connection);
    }

    public DISARoleResource createDISARole(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists(DISARole.QUERY_ROLE_EXISTS, new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(6));
        DISARole newRole = new DISARole();
        newRole.setRoleType(type);
        return new DISARoleResource(newRole, connection);
    }

    public BPCRoleResource createBPCRole(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        checkRoleExists(BPCRole.QUERY_ROLE_EXISTS, new Pair<String, Object>("objectName", identifier), service);

        RoleType type = service.findById(RoleType.class, Integer.valueOf(6));
        BPCRole newRole = new BPCRole();
        newRole.setRoleType(type);
        return new BPCRoleResource(newRole, connection);
    }

    public ConflictResource createConflict(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        Object result = service.findNamedObject("conflict.exists", new Pair<String, Object>("objectName", identifier));
        checkExists(result);

        Conflict newConflict = new Conflict();
        newConflict.setCodeName(identifier);

        ConflictDetails details = new ConflictDetails();
        details.setConflict(newConflict);
        newConflict.getConflictChangeHistory().add(details);
        return new ConflictResource(newConflict, connection);
    }

    public MitigationControlResource createMitigationControl(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        Object result = service.findNamedObject(MitigationControl.QUERY_ISEXISTS, new Pair<String, Object>("objectName", identifier));
        checkExists(result);

        MitigationControl newCtrl = new MitigationControl();
        newCtrl.setCodeName(identifier);

        MitigationControlDetails details = new MitigationControlDetails();
        details.setMitigationControl(newCtrl);
        newCtrl.getMitigationControlChangeHistory().add(details);
        return new MitigationControlResource(newCtrl, connection);
    }

    public RiskResource createRisk(ICIMSConnection connection, String identifier) {
        checkConnection(connection);
        IConnectorService service = connection.getService();
        Object result = service.findNamedObject(Risk.QUERY_ISEXISTS, new Pair<String, Object>("objectName", identifier));
        checkExists(result);

        Risk risk = new Risk();
        risk.setCodeName(identifier.toUpperCase());

        RiskDetails details = new RiskDetails();
        details.setRisk(risk);
        //risk.getRiskChangeHistory().add(details);
        risk.setLastObjectDetails(details);
        return new RiskResource(risk, connection);
    }

    public <T> T createFromMetaResource(ICIMSConnection connection, MetaResource resource, String identifier) {
        Class<?> type = resource.getEntityType();
        String methodName = "create" + type.getSimpleName();
        RuntimeException exception = null;
        T result = null;
        try {
            Method createMethod = getClass().getMethod(methodName, ICIMSConnection.class, String.class);
            result = (T) createMethod.invoke(this, connection, identifier);
        } catch (SecurityException e) {
            exception = new RuntimeException(err(NOT_ACCESSIBLE, methodName), e);
        } catch (NoSuchMethodException e) {
            exception = new UnsupportedOperationException(err(NOT_IMPLEMENTED, methodName), e);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = new RuntimeException(err(NOT_ACCESSIBLE, methodName), e);
        } catch (InvocationTargetException e) {
            exception = new RuntimeException(CANNOT_CREATE, e);
        }

        if (exception != null) {
            throw exception;
        }

        return result;
    }

    private void checkRoleExists(String query, Pair<String, Object> identifier, IConnectorService service) {
        Object result = service.findNamedObject(query, identifier);
        checkExists(result);
    }

    private void checkExists(Object result) {
        if (result != null && ((Long) result) > 0) {
            throw new AlreadyExistsException();
        }
    }

    private static Object singleResult(Collection<?> results) {
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results.iterator().next();
    }

    private static void checkConnection(ICIMSConnection connection) {
        if (connection == null) {
            throw new IllegalStateException("Connection was null");
        }
        if (connection.getService() == null) {
            throw new IllegalStateException("Connector service was null. Unable to create entities...");
        }
    }

    private String err(String key, String... args) {
        return String.format(key, args);
    }
}
