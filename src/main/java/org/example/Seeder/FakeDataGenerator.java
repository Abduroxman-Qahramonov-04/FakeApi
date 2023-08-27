package org.example.Seeder;
import com.github.javafaker.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class FakeDataGenerator {
    static Faker faker = new Faker();
    static Name name = faker.name();
    static Country country = faker.country();
    static Address address = faker.address();
    static Book book = faker.book();
    static Job job = faker.job();
    static Lorem lorem = faker.lorem();

    static PhoneNumber phoneNumber = faker.phoneNumber();
    static Map<FieldType,Supplier<Object>> functions = new HashMap<>();
    static {

        functions.put(FieldType.FIRST_NAME, name::firstName);
        functions.put(FieldType.LAST_NAME, name::lastName);
        functions.put(FieldType.CELL_PHONE,  phoneNumber::cellPhone);
        functions.put(FieldType.AGE, ()-> faker.random().nextInt(1,100));
        functions.put(FieldType.CITY, country::capital);
        functions.put(FieldType.COUNTRY, country::name);
        functions.put(FieldType.ZIP_CODE, address::zipCode);
        functions.put(FieldType.BOOK_AUTHOR,  book::author);
        functions.put(FieldType.TITLE, job::title);
        functions.put(FieldType.USERNAME, name::firstName);
        functions.put(FieldType.BOOK_PUBLISHER, book::publisher);
        functions.put(FieldType.BOOK_TITLE, book::title);
        functions.put(FieldType.CHARACTERS,  lorem::character);
        functions.put(FieldType.WORDS, lorem::words);
        functions.put(FieldType.BLOOD_GROUP, name::bloodGroup);
        functions.put(FieldType.BOOK_GENRE, book::genre);
        functions.put(FieldType.COUNTRY_CODE, country::countryCode2);

    }

    public static void main(String[] args) throws IOException {


        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name:");
        String fileName = sc.next();
        System.out.println("Enter File Type(JSON, CSV,SQL) :");
        String type = sc.next();
        System.out.println("Enter Rows Count");
        int count = sc.nextInt();
        String temp = "";
        List<Pairs> pppairs = new ArrayList<>();
        while(!temp.equals("s")){

            System.out.println("Enter the filed name:");
            String fieldName = sc.next();

            int t = 1;

            for (FieldType fieldType: FieldType.values()) {
                if((t+1)%2==0)
                    System.out.println();
                System.out.print(t++ +".  "+fieldType+"\t\t");
            }System.out.println("\nEnter the field type(1-17):");
            int flType = sc.nextInt();
            FieldType type_field = FieldType.values()[flType-1];
            pppairs.add(new Pairs(fieldName,type_field));

            System.out.println("Add a file-> y(es)");
            System.out.println("Stop adding a file->s(top)");
            temp = sc.next();
        }

        Request request = new Request(fileName+"."+type,count,type,pppairs);
        switch (type) {
            case "json" -> generateRandomDataAsJson(functions, request);
            case "csv" -> generateRandomDataAsCSV(functions, request);
            case "sql" -> generateRandomDataAsSQL(functions, request);
            default -> System.out.println("Sorry but we do not support this type of file!");
        }
    }
    private static void generateRandomDataAsCSV(Map<FieldType, Supplier<Object>> functions, Request request) throws IOException {
        List<Pairs> pairs = request.getPairs();
        StringBuilder div = new StringBuilder();
        String firstLine = "";

        for (int i = 0; i < pairs.size(); i++)
            firstLine += pairs.get(i).getFieldName()+((i!=pairs.size()-1)?",":"");

        for (int i = 0; i < request.getCount(); i++) {
            StringBuilder horizontal = new StringBuilder();
            for (Pairs pair : pairs) {
                FieldType fieldType = pair.getFieldType();
                Object value = functions.get(fieldType).get();
                String result = value.toString();
                if (result.contains(",")) {
                    result = "\"" + result + "\"";
                }
                horizontal.append(result).append(",");
            }
            div.append(horizontal.substring(0, horizontal.length() - 1)).append("\n");
        }
        String res = firstLine+"\n"+div;
        Files.writeString(Path.of(request.getFileName()),res);
        System.out.println(res);
    }

    private static void generateRandomDataAsJson(Map<FieldType, Supplier<Object>> functions, Request request) throws IOException {
        List<Pairs> pairs = request.getPairs();
        StringJoiner stringJoiner = new StringJoiner(", ","[\n","\n]");
        for (int i = 0; i < request.getCount(); i++) {
            StringJoiner stringJoiner1 = new StringJoiner(",\n","\n{\n","\n}");
            for(Pairs pair: pairs){
                FieldType fieldType = pair.getFieldType();
                stringJoiner1.add(fieldType.getJsonPairs(fieldType, functions.get(fieldType).get()));
            }
            stringJoiner.add(stringJoiner1.toString());
        }
        Files.writeString(Path.of(request.getFileName()),stringJoiner.toString());
    }
    private static void generateRandomDataAsSQL(Map<FieldType, Supplier<Object>> functions, Request request) throws IOException{
        List<Pairs> pairs = request.getPairs();
        StringJoiner html = new StringJoiner("");
        String tableName ="INSERT INTO "+request.getFileName().replace(".","_")+" ";


        StringJoiner headHtml = new StringJoiner("");
        for (int i = 0; i < request.getCount(); i++) {
            StringJoiner keys = new StringJoiner(",","(",")");
            StringJoiner values = new StringJoiner(",","(",")");
            for (Pairs p : pairs) {
                FieldType fieldType = p.getFieldType();
                keys.add(p.getFieldName());
                values.add(fieldType.getSQLPairs(functions.get(fieldType).get()));
            }
            html.add(tableName);
            html.add(keys.toString());
            html.add(" VALUES "+values+";\n");
        }
        System.out.println(headHtml);
        Files.writeString(Path.of(request.getFileName()),headHtml.toString());
    }
}
