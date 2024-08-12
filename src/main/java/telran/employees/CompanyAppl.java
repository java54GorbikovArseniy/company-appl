package telran.employees;

import telran.io.Persistable;
import telran.view.Item;
import telran.view.Menu;
import telran.view.SystemInputOutput;

import java.util.HashSet;
import java.util.List;

public class CompanyAppl {
    private static final String FILE_NAME = "employeesTest.data";

    public static void main(String[] args) {
        Company company = new CompanyMapsImpl();
        try {
            ((Persistable) company).restore(FILE_NAME);
        } catch (Exception e) {
        }
        List<Item> companyItems = CompanyApplItems.getCompanyItems(
                company, new HashSet<String>(List.of("Audit", "Development", "QA")));
        companyItems.add(Item.of("Exit & save", inputOutput -> ((Persistable) company).save(FILE_NAME), true));
        companyItems.add(Item.ofExit());
        Menu menu = new Menu("Company CLI Application", companyItems.toArray(Item[]::new));
        menu.perform(new SystemInputOutput());
    }
}
