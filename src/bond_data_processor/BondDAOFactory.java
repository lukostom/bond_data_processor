package bond_data_processor;


/**
 * @author tom
 * Factory class to return DAO object
 * It may be needed when there is
 * more than 1 database e.g. 
 * one DAO for MySQL and another for PostgreSQL
 */
public class BondDAOFactory {

	public static BondDAO createBondDAO() {
		// TODO Auto-generated method stub
		return new BondDAO();
	}

}
