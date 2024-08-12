package telran.employees;

import telran.view.InputOutput;
import telran.view.Item;
import telran.view.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class CompanyApplItems {
    static Company company;
    static HashSet<String> departments;

    public static List<Item> getCompanyItems(Company company, HashSet<String> departments) {
        CompanyApplItems.company = company;
        CompanyApplItems.departments = departments;
        Item[] items = {
                Item.of("add employee", (InputOutput inputOutput) -> {
                    addEmployee(inputOutput);
                }),
                Item.of("display employee data", CompanyApplItems::getEmployee),
                Item.of("remove employee", CompanyApplItems::removeEmployee),
                Item.of("display department budget", CompanyApplItems::getDepartmentBudget),
                Item.of("display departments", CompanyApplItems::getDepartments),
                Item.of("display managers with most factor", CompanyApplItems::getManagersWithMostFactor),
        };
        return new ArrayList<>(List.of(items));
    }

    private static void getManagersWithMostFactor(InputOutput inputOutput) {
        Manager[] managers = company.getManagersWithMostFactor();
        String line = managers.length == 0 ? "no managers" :
                Arrays.stream(managers).map(Employee::getJSON)
                        .collect(Collectors.joining("\n"));
        inputOutput.writeLine(line);
    }

    private static void getDepartments(InputOutput inputOutput) {
        String departments = company.getDepartments().length == 0 ?
                "No department has been created yet" :
                Arrays.stream(company.getDepartments()).map(dep -> dep + " ").collect(Collectors.joining());
        inputOutput.writeLine(departments);
    }

    private static void getDepartmentBudget(InputOutput inputOutput) {
        String department = inputOutput.readStringOptions("Enter department ", "Wrong department or no employees woring in entered department", new HashSet<>(List.of(company.getDepartments())));
        inputOutput.writeLine("The budget of " + department + " department : " + company.getDepartmentBudget(department));
    }

    private static void removeEmployee(InputOutput inputOutput) {
        long id = getId(inputOutput);
        company.removeEmployee(id);
        inputOutput.writeLine("Employee was successful removed");
    }

    private static void getEmployee(InputOutput inputOutput) {
        long id = getId(inputOutput);
        Employee empl = company.getEmployee(id);
        String line = empl == null ? "no employee with the entered ID"
                : empl.getJSON();
        inputOutput.writeLine(line);
    }

    private static void addEmployee(InputOutput inputOutput) {
        Item[] items = new Item[]{
                Item.of("WageEmployee", io -> addEmployeeItem(io, CompanyApplItems::getWageEmployee)),
                Item.of("SalesPerson", io -> addEmployeeItem(io, CompanyApplItems::getSalesPerson)),
                Item.of("Manager", io -> addEmployeeItem(io, CompanyApplItems::getManager)),
                Item.ofExit()};
        Menu menu = new Menu("Choose Employee type", items);
        menu.perform(inputOutput);
        inputOutput.writeLine("=".repeat(40));
    }

    static private void addEmployeeItem(InputOutput io,
                                        BiFunction<Employee, InputOutput, Employee> actualAdding) {
        Employee empl = readEmployee(io);
        Employee result = actualAdding.apply(empl, io);
        company.addEmployee(result);
        io.writeLine("Employee has been added");
        io.writeLine("=".repeat(40));
    }

    private static Employee getSalesPerson(Employee employee, InputOutput inputOutput) {
        WageEmployee wageEmployee = (WageEmployee) getWageEmployee(employee, inputOutput);
        float percents = inputOutput.readNumberRange("Enter percents", "Wrong percents value", 0.5, 2).floatValue();
        long sales = inputOutput.readNumberRange("Enter Sales", "Wrong sales valut", 500, 50000).longValue();
        return new SalesPerson(
                employee.getId(),
                employee.getBasicSalary(),
                employee.getDepartment(),
                wageEmployee.getHours(),
                wageEmployee.getWage(),
                percents,
                sales);
    }

    private static Employee getManager(Employee employee, InputOutput inputOutput) {
        float factor = inputOutput.readNumberRange("Enter factor", "Wrong factor", 1.5, 5).floatValue();
        return new Manager(employee.getId(), employee.getBasicSalary(), employee.getDepartment(), factor);
    }

    private static Employee getWageEmployee(Employee employee, InputOutput inputOutput) {
        int hours = inputOutput.readNumberRange("Enter working hours", "Wrong working hours value", 10, 200).intValue();
        int wage = inputOutput.readNumberRange("Enter hour wage", "Wrong wage value", 100, 1000).intValue();
        return new WageEmployee(employee.getId(), employee.getBasicSalary(), employee.getDepartment(), hours, wage);
    }

    private static Employee readEmployee(InputOutput inputOutput) {
        long id = getId(inputOutput);
        int basicSalary = inputOutput.readNumberRange("Enter basic salary", "Wrong basic salary value", 2000, 20000).intValue();
        ;
        String department = inputOutput.readStringOptions("EnterDepartment " + departments, "Wrong department", departments);
        return new Employee(id, basicSalary, department);
    }

    private static long getId(InputOutput inputOutput) {
        long id = inputOutput.readNumberRange("Enter id value", "Wrong id value", 1000, 10000).longValue();
        return id;
    }
}
