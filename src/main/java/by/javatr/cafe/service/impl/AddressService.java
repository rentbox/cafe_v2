package by.javatr.cafe.service.impl;

import by.javatr.cafe.container.annotation.Component;
import by.javatr.cafe.dao.DAOFactory;
import by.javatr.cafe.dao.repository.IAddressRepository;
import by.javatr.cafe.dao.repository.IUserRepository;
import by.javatr.cafe.entity.Address;
import by.javatr.cafe.entity.User;
import by.javatr.cafe.exception.DAOException;
import by.javatr.cafe.exception.ServiceException;
import by.javatr.cafe.service.IAddressService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for work with addresses
 */
@Component
public class AddressService implements IAddressService {

    public static final Logger logger = LogManager.getLogger(AddressService.class);

    private AddressService(){}

    /**
     * Update address
     * @param address to be updated
     * @return updated address
     */
    @Override
    public Address update(Address address) throws ServiceException {

        try(DAOFactory factory = new DAOFactory()) {
            IAddressRepository addressRepository = factory.getAddressRepository();

            Address update = addressRepository.update(address);
            if(update == null) throw new ServiceException("invalid update address");
            return address;
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    /**
     *  Returns all addresses
     * @return list of addresses
     */
    @Override
    public List<Address> getAll() throws ServiceException {

        List<Address> all = null;
        try (DAOFactory factory = new DAOFactory();){
            IAddressRepository addressRepository = factory.getAddressRepository();
            all = addressRepository.getAll();
        } catch (DAOException e) {
            throw new ServiceException("get all addresses service ex",e);
        }
        return all;

    }

    /**
     *  Returns list of user addresses
     * @param userId user id
     * @return  list of addresses addresses
     */
    @Override
    public List<Address> getAllForUser(int userId) throws ServiceException {

        List<Address> allId = null;
        try (DAOFactory factory = new DAOFactory()){
            IAddressRepository addressRepository = factory.getAddressRepository();
            allId = addressRepository.getAllId(userId);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return allId;
    }

    /**
     * Create address
     * @param address to be created
     * @return created address
     */
    @Override
    public Address create(Address address) throws ServiceException {

        DAOFactory factory = new DAOFactory();

        try {
            IAddressRepository addressRepository = factory.getAddressRepository();
            IUserRepository userRepository = factory.getUserRepository();

            address = addressRepository.create(address);
            if(address != null){
                final User user = userRepository.findUser(new User(address.getUserId()));
                if (user.getAddress() == null) {
                    user.setAddress(new ArrayList<>());
                }
                return address;
            }else{
                return null;
            }
        } catch (DAOException e) {
            factory.rollback();
            throw new ServiceException("failed to create address ",e);
        }finally {
            factory.endTransaction();
        }
    }

    /**
     * Find address
     * @param address being found
     * @return found address
     */
    @Override
    public Address find(Address address) throws ServiceException {

        try(DAOFactory factory = new DAOFactory()) {
            IAddressRepository addressRepository = factory.getAddressRepository();

            address = addressRepository.get(address);
            return address ;
        } catch (DAOException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * Delete address
     * @param address to be deleted
     * @return boolean
     */
    @Override
    public boolean delete(Address address) throws ServiceException {
        try(DAOFactory factory = new DAOFactory()) {
            IAddressRepository addressRepository = factory.getAddressRepository();
            return addressRepository.delete(address);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
