package pl.coderampart.controller.admin;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import pl.coderampart.DAO.MentorDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pl.coderampart.model.Mentor;

import java.io.*;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditMentorController implements HttpHandler {

    private Connection connection;
    private MentorDAO mentorDAO;

    public EditMentorController(Connection connection) {
        this.connection = connection;
        this.mentorDAO = new MentorDAO(this.connection);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String response = "";

        String[] uri = httpExchange.getRequestURI().toString().split("=");
        String id = uri[uri.length-1];

        List<Mentor> allMentors = readMentorsFromDB();

        if(method.equals("GET")) {        
            response += renderHeader(httpExchange);
            response += render("header");
            response += render("admin/adminMenu");
            String responseTemp = renderMentorsList(allMentors);
            if(id.length()==36) {
                responseTemp = renderEditMentor(getMentorById(id, allMentors), allMentors);
            }
            response += responseTemp;
            response += render("footer");
        }

        if(method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader( httpExchange.getRequestBody(), "utf-8" );
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            Map inputs = parseFormData(formData);

            editMentor(inputs, allMentors, id);

            response += render("header");
            response += render("admin/adminMenu");
            String responseTemp = renderMentorsList(allMentors);
            if(id.length()==36) {

                responseTemp = renderEditMentor(getMentorById(id, allMentors), allMentors);
            }
            response +=responseTemp;
            response += render("footer");
        }

        httpExchange.sendResponseHeaders( 200, response.getBytes().length );
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());

        os.close();
    }

    private void editMentor(Map inputs, List<Mentor>allMentors, String id) {


        Mentor changedMentor = getMentorById(id, allMentors);

        String firstName = String.valueOf(inputs.get("first-name"));
        String lastName= String.valueOf(inputs.get("last-name"));
        String dateOfBirth = String.valueOf(inputs.get("date-of-birth"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(dateOfBirth, formatter);
        String email= String.valueOf(inputs.get("email"));


        if(!changedMentor.equals(null)){
            changedMentor.setFirstName(firstName);
            changedMentor.setLastName(lastName);
            changedMentor.setDateOfBirth(date);
            changedMentor.setEmail(email);
            try{
                mentorDAO.update(changedMentor);
            }catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    private Mentor getMentorById(String id, List<Mentor> allMentors) {
        Mentor changedMentor = null;

        for (Mentor mentor: allMentors) {
            if (id.equals(mentor.getID())) {
                changedMentor = mentor;
                changedMentor.setFirstName(firstName);
                changedMentor.setLastName(lastName);
                changedMentor.setDateOfBirth(date);
                changedMentor.setEmail(email);
                try{
                    mentorDAO.update(changedMentor);
                } catch (SQLException se){}
                break;
            }
        }
        return changedMentor;
    }

    private List<Mentor> readMentorsFromDB() {
        List <Mentor> allMentors = null;

        try {
            allMentors = mentorDAO.readAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allMentors;
    }

    private String render(String fileName) {
        String templatePath = "templates/" + fileName + ".twig";
        JtwigTemplate template = JtwigTemplate.classpathTemplate( templatePath );
        JtwigModel model = JtwigModel.newModel();


        return template.render(model);
    }

    private String renderMentorsList(List<Mentor> allMentors) {
        String templatePath = "templates/admin/editMentor.twig";
        JtwigTemplate template = JtwigTemplate.classpathTemplate( templatePath );
        JtwigModel model = JtwigModel.newModel();
        model.with("allMentors", allMentors);

        return template.render(model);
    }

    private String renderEditMentor(Mentor mentor, List<Mentor> allMentors) {

        String templatePath = "templates/admin/editMentor.twig";
        JtwigTemplate template = JtwigTemplate.classpathTemplate( templatePath );
        JtwigModel model = JtwigModel.newModel();

        model.with("allMentors", allMentors);
        model.with("firstName", mentor.getFirstName());
        model.with("lastName", mentor.getLastName());
        model.with("email", mentor.getEmail());
        model.with("dateOfBirth", mentor.getDateOfBirth());

        return template.render(model);
    }

    private String renderHeader(HttpExchange httpExchange) {
        Map<String, String> cookiesMap = createCookiesMap( httpExchange );

        String templatePath = "templates/header.twig";
        JtwigTemplate template = JtwigTemplate.classpathTemplate( templatePath );
        JtwigModel model = JtwigModel.newModel();

        model.with("firstName", cookiesMap.get("firstName") );
        model.with("lastName", cookiesMap.get("lastName") );
        model.with("userType", cookiesMap.get("typeOfUser") );

        return  template.render(model);
    }

    private Map<String, String> createCookiesMap(HttpExchange httpExchange) {
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        String[] cookiesValues = cookieStr.split("; ");

        Map<String, String> cookiesMap = new HashMap<>();

        for (String cookie : cookiesValues) {
            String[] nameValuePairCookie = cookie.split("=\"");
            String name = nameValuePairCookie[0];
            String value = nameValuePairCookie[1].replace("\"", "");

            cookiesMap.put(name, value);
        }
        return cookiesMap;
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}
