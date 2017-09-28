package pl.coderampart.DAO;

import pl.coderampart.model.*;
import pl.coderampart.model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class ItemDAO extends AbstractDAO {

    private ArtifactDAO artifactDAO = new ArtifactDAO();
    private WalletDAO walletDAO = new WalletDAO();

    public ArrayList<Item> readAll() {
        ArrayList<Item> itemList = new ArrayList<>();

        try {
            Connection connection = this.connectToDataBase();
            String query = "SELECT * FROM items;";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Item item = this.createItemFromResultSet(resultSet);
                itemList.add(item);
            }
            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return itemList;
    }

    public ArrayList<Item> getUserItems(String walletID) {
        ArrayList<Item> itemList = new ArrayList<>();

        try {
            Connection connection = this.connectToDataBase();
            String query = "SELECT * FROM items WHERE wallet_id = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, walletID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Item item = this.createItemFromResultSet(resultSet);
                itemList.add(item);
            }
            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return itemList;
    }

    public void create(Item item) {

        try {
            Connection connection = this.connectToDataBase();
            String query = "INSERT INTO items VALUES (?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            PreparedStatement setStatement = setPreparedStatement(statement, item);
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void update(Item item) {
        try {
            Connection connection = this.connectToDataBase();
            String query = "UPDATE items SET id = ?, artifact_id = ?, wallet_id = ?, " +
                           "creation_date = ?, is_spent = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            PreparedStatement setStatement = setPreparedStatement(statement, item);
            setStatement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void delete(Item item) {
        try {
            Connection connection = this.connectToDataBase();
            String query = "DELETE FROM items WHERE ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, item.getID());
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    private PreparedStatement setPreparedStatement(PreparedStatement statement, Item item) throws Exception {
        statement.setString(1, item.getID());
        statement.setString(2, item.getArtifact().getID());
        statement.setString(3, item.getWallet().getID());
        statement.setString(4, item.getCreationDate().toString());
        statement.setBoolean(5, item.getMark());

        return statement;
    }

    private Item createItemFromResultSet(ResultSet resultSet) throws Exception {
        String ID = resultSet.getString("id");
        String artifact_id = resultSet.getString("artifact_id");
        Artifact artifact = artifactDAO.getByID(artifact_id);
        String wallet_id = resultSet.getString("wallet_id");
        Wallet wallet = walletDAO.getByID(wallet_id);
        String creationDate = resultSet.getString("cration_date");
        LocalDate creationDateObject = LocalDate.parse(creationDate);
        Boolean isSpent = resultSet.getBoolean("is_spent");

        return new Item(ID, artifact, wallet, creationDateObject, isSpent);
    }
}