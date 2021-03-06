package pl.coderampart.controller.admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pl.coderampart.DAO.GroupDAO;
import pl.coderampart.controller.helpers.AccessValidator;
import pl.coderampart.controller.helpers.FlashNoteHelper;
import pl.coderampart.controller.helpers.HelperController;
import pl.coderampart.model.Group;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class CreateGroupController extends AccessValidator implements HttpHandler {

    private Connection connection;
    private GroupDAO groupDAO;
    private HelperController helper;
    private FlashNoteHelper flashNoteHelper;

    public CreateGroupController(Connection connection) {
        this.connection = connection;
        this.groupDAO = new GroupDAO( connection );
        this.helper = new HelperController(connection);
        this.flashNoteHelper = new FlashNoteHelper();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        validateAccess( "Admin", httpExchange, connection);
        String response = "";
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")) {
            response += helper.renderHeader(httpExchange, connection);
            response += helper.render("admin/adminMenu");
            response += helper.render("admin/createGroup");
            response += helper.render("footer");

            helper.sendResponse( response, httpExchange );
        }

        if (method.equals("POST")) {
            Map<String, String> inputs = helper.getInputsMap( httpExchange );

            createGroup( inputs, httpExchange );
            helper.redirectTo( "/group/create", httpExchange );
        }
    }

    public void createGroup(Map<String, String> inputs, HttpExchange httpExchange) {
        String groupName = inputs.get("group-name");
        Group newGroup = new Group(groupName);

        try {
            groupDAO.create(newGroup);

            String flashNote = flashNoteHelper.createCreationFlashNote( "Group", groupName );
            flashNoteHelper.addSuccessFlashNoteToCookie(flashNote, httpExchange);
        } catch (SQLException e) {
            flashNoteHelper.addFailureFlashNoteToCookie(httpExchange);
            e.printStackTrace();
        }
    }
}
