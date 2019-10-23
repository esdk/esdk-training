package de.abas.esdk.g30l0;

import de.abas.erp.db.Deletable;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.esdk.test.util.EsdkIntegTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestData.CUSTOMER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeolocationInfosystemTest extends EsdkIntegTest {

	@BeforeClass
	public static void createTestData() {
		CustomerEditor customerEditor = ctx.newObject(CustomerEditor.class);
		try {
			customerEditor.setSwd(CUSTOMER.swd);
			customerEditor.setStreet(CUSTOMER.street);
			customerEditor.setZipCode(CUSTOMER.zipCode);
			customerEditor.setTown(CUSTOMER.town);
			customerEditor.commit();
			CUSTOMER.tradingPartner = customerEditor.objectId();
		} finally {
			if (customerEditor.active()) {
				customerEditor.abort();
			}
		}
	}

	public GeoLocation geoLocation = ctx.openInfosystem(GeoLocation.class);

	@Test
	public void canDisplayCustomer() {
		geoLocation.setCustomersel(CUSTOMER.swd);
		geoLocation.invokeStart();
		assertThat(geoLocation.table().getRowCount(), is(1));
		assertThat(geoLocation.table().getRow(1).getCustomer().getSwd(), is(CUSTOMER.swd));
		assertThat(geoLocation.table().getRow(1).getZipcode(), is(CUSTOMER.zipCode));
		assertThat(geoLocation.table().getRow(1).getTown(), is(CUSTOMER.town));
		assertThat(geoLocation.table().getRow(1).getState().getSwd(), is(CUSTOMER.country));
	}

	@After
	public void cleanup() {
		geoLocation.abort();
	}

	@AfterClass
	public static void deleteTestData() {
		for (final TestData testData : TestData.values()) {
			if (testData.tradingPartner instanceof Deletable) {
				((Deletable) testData.tradingPartner).delete();
			}
		}
	}

	enum TestData {
		CUSTOMER("TESTCUST", "Gartenstrasse 67", "76135", "Karlsruhe", "DEUTSCHLAND");

		private final String swd;
		private final String street;
		private final String zipCode;
		private final String town;
		private final String country;
		TradingPartner tradingPartner = null;

		TestData(final String swd, final String street, final String zipCode, final String town, final String country) {
			this.swd = swd;
			this.street = street;
			this.zipCode = zipCode;
			this.town = town;
			this.country = country;
		}
	}

}
