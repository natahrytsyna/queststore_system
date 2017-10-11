package pl.coderampart.controller;

import pl.coderampart.DAO.*;
import pl.coderampart.enums.*;
import pl.coderampart.model.*;
import pl.coderampart.services.Bootable;
import pl.coderampart.view.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class MentorController implements Bootable<Mentor> {

    private Mentor selfMentor;
    private MentorView mentorView = new MentorView();
    private CodecoolerDAO codecoolerDAO;
    private TeamDAO teamDAO;
    private ArtifactDAO artifactDAO;
    private AchievementDAO achievementDAO;
    private ItemDAO itemDAO;
    private QuestDAO questDAO;
    private Connection connection;

    public MentorController(Connection connectionToDB) {
        connection = connectionToDB;
        codecoolerDAO = new CodecoolerDAO(connection);
        teamDAO = new TeamDAO(connection);
        artifactDAO = new ArtifactDAO(connection);
        achievementDAO = new AchievementDAO(connection);
        itemDAO = new ItemDAO(connection);
        questDAO = new QuestDAO(connection);
    }


    public boolean start(Mentor mentor) throws SQLException{
        mentorView.displayMentorManagementMenu();
        selfMentor = mentor;

        int userChoice = mentorView.getUserChoice();
        MentorSubmenuOption mentorSubmenuOption = MentorSubmenuOption.values()[userChoice];
        mentorView.clearTerminal();

        switch(mentorSubmenuOption) {
            case DISPLAY_CODECOOLER_MANAGEMENT_MENU:
                startCodecoolerMM();
                break;
            case DISPLAY_QUEST_MANAGEMENT_MENU:
                startQuestMM();
                break;
            case DISPLAY_ARTIFACT_MANAGEMENT_MENU:
                startArtifactMM();
                break;
            case DISPLAY_TEAM_MANAGEMENT_MENU:
                startTeamMM();
                break;
            case EXIT:
                return false;
        }
        mentorView.enterToContinue();
        return true;
    }

    public boolean startCodecoolerMM() throws SQLException {
        mentorView.displayCodecoolerMM();
        int userChoice = mentorView.getUserChoice();
        CodecoolerMMOptions codecoolerMMOptions = CodecoolerMMOptions.values()[userChoice];
        mentorView.clearTerminal();

        switch (codecoolerMMOptions){
            case CREATE_CODECOOLER: createCodecooler();
                break;
            case EDIT_CODECOOLER: editCodecooler();
                break;
            case DISPLAY_CODECOOLERS: displayCodecoolers();
                break;
            case CREATE_ACHIEVEMENT: createAchievement();
                break;
            case MARK_ITEM: markItem();
                break;
            case DISPLAY_WALLET: displayWallet();
                break;
            case BACK_TO_MAIN_MENU:
                return false;
        }
        return true;
    }

    public boolean startQuestMM() throws SQLException {
        mentorView.displayQuestMM();
        int userChoice = mentorView.getUserChoice();

        QuestMMOptions questMMOptions = QuestMMOptions.values()[userChoice];
        mentorView.clearTerminal();
        switch (questMMOptions){
            case CREATE_QUEST: createQuest();
                break;
            case EDIT_QUEST: editQuest();
                break;
            case DISPLAY_QUESTS: displayQuests();
                break;
            case DELETE_QUEST: deleteQuest();
                break;
            case BACK_TO_MAIN_MENU:
                return false;
        }
        return true;
    }

    public boolean startArtifactMM() throws SQLException {
        mentorView.displayArtifactMM();
        int userChoice = mentorView.getUserChoice();

        ArtifactMMOptions artifactMMOptions = ArtifactMMOptions.values()[userChoice];
        mentorView.clearTerminal();
        switch (artifactMMOptions){
            case CREATE_ARTIFACT: createArtifact();
                break;
            case EDIT_ARTIFACT: editArtifact();
                break;
            case DISPLAY_ARTIFACTS: displayArtifacts();
                break;
            case DELETE_ARTIFACT: deleteArtifact();
                break;
            case BACK_TO_MAIN_MENU:
                return false;
        }
        return true;
    }

    public boolean startTeamMM() throws SQLException {
        mentorView.displayTeamMM();
        int userChoice = mentorView.getUserChoice();

        TeamMMOptions teamMMOptions = TeamMMOptions.values()[userChoice];
        mentorView.clearTerminal();
        switch (teamMMOptions){
            case CREATE_TEAM: createTeam();
                break;
            case EDIT_TEAM: editTeam();
                break;
            case DISPLAY_TEAMS: displayTeams();
                break;
            case DELETE_TEAM: deleteTeam();
                return false;
        }
        return true;
    }

    public void createCodecooler() throws SQLException {
        String[] codecoolerData = mentorView.getUserData();

        Codecooler newCodecooler = new Codecooler(codecoolerData[0], codecoolerData[1], mentorView.stringToDate(codecoolerData[2]),
                                                  codecoolerData[3], codecoolerData[4], connection);

        this.displayTeams();
        Team chosenTeam = getTeamByName();
        newCodecooler.setTeam(chosenTeam);
        newCodecooler.setGroup(selfMentor.getGroup());

        codecoolerDAO.create(newCodecooler);
    }

    public void createQuest() throws SQLException {
        String[] questData = mentorView.getQuestData();

        Quest newQuest = new Quest(questData[0], questData[1], Integer.valueOf(questData[2]));

        questDAO.create(newQuest);
    }

    public void createArtifact() throws SQLException {
        String[] artifactData = mentorView.getArtifactData();

        Artifact newArtifact = new Artifact(artifactData[0], artifactData[1], artifactData[2], Integer.valueOf(artifactData[3]));

        artifactDAO.create(newArtifact);
    }

    public void createTeam() throws SQLException {
        String teamName = mentorView.getInput("Enter name of a new team: ");

        Team newTeam = new Team(teamName, selfMentor.getGroup());

        teamDAO.create(newTeam);
    }

    public void editCodecooler() throws SQLException{
        Codecooler changedCodecooler = getCodecoolerByEmail();

        if (changedCodecooler != null) {
            mentorView.displayEditCodecoolerMenu();
            int userChoice = mentorView.getUserChoice();
            MentorEditCodecooler editCodecoolerOptions = MentorEditCodecooler.values()[userChoice];
            mentorView.clearTerminal();

            switch(editCodecoolerOptions){
                case EDIT_FIRST_NAME:
                    changedCodecooler.setFirstName(mentorView.getInput("Enter new name: "));
                    break;
                case EDIT_LAST_NAME:
                    changedCodecooler.setLastName(mentorView.getInput("Enter new name: "));
                    break;
                case EDIT_EMAIL:
                    changedCodecooler.setEmail(mentorView.getRegExInput(mentorView.emailRegEx, "Enter new email: "));
                    break;
                case EDIT_PASSWORD:
                    changedCodecooler.setPassword(mentorView.getInput("Enter new password: "));
                    break;
                case EDIT_BIRTHDATE:
                    changedCodecooler.setDateOfBirth(mentorView.stringToDate(mentorView.getRegExInput(mentorView.dateRegEx,
                            "Enter new date")));
                    break;
            }

            codecoolerDAO.update(changedCodecooler);
        }
    }

    public void editQuest() throws SQLException {
        Quest changedQuest = getQuestByName();

        if (changedQuest != null){
            mentorView.displayEditQuestMenu();
            int userChoice = mentorView.getUserChoice();
            MentorEditQuest editQuestOptions = MentorEditQuest.values()[userChoice];
            mentorView.clearTerminal();

            switch(editQuestOptions){
                case EDIT_NAME:
                    changedQuest.setName(mentorView.getInput("Enter new name: "));
                    break;
                case EDIT_DESCRIPTION:
                    changedQuest.setDescription(mentorView.getInput("Enter new description: "));
                    break;
                case EDIT_REWARD:
                    changedQuest.setReward(Integer.valueOf(mentorView.getInput("Enter new reward: ")));
                    break;
            }

            questDAO.update(changedQuest);
        }
    }

    public void editArtifact() throws SQLException {
        Artifact changedArtifact = getArtifactByName();

        if (changedArtifact != null){
            mentorView.displayEditArtifactMenu();
            int userChoice = mentorView.getUserChoice();
            MentorEditArtifact editArtifactOptions = MentorEditArtifact.values()[userChoice];
            mentorView.clearTerminal();

            switch(editArtifactOptions){
                case EDIT_NAME:
                    changedArtifact.setName(mentorView.getInput("Enter new name: "));
                    break;
                case EDIT_DESCRIPTION:
                    changedArtifact.setDescription(mentorView.getInput("Enter new description: "));
                    break;
                case EDIT_TYPE:
                    changedArtifact.setType(mentorView.getInput("Enter new type: "));
                    break;
                case EDIT_VALUE:
                    changedArtifact.setValue(Integer.valueOf(mentorView.getInput("Enter new value: ")));
                    break;
            }

            artifactDAO.update(changedArtifact);
        }
    }

    public void editTeam() throws SQLException {
        Team changedTeam = getTeamByName();

        if (changedTeam != null){
            changedTeam.setName(mentorView.getInput("Enter new name: "));
            teamDAO.update(changedTeam);
        }
    }

    public void displayCodecoolers() throws SQLException {
        ArrayList<Codecooler> allCodecoolers = codecoolerDAO.readAll();
        ArrayList<String> codecoolerStrings = new ArrayList<String>();

        for (Codecooler codecooler: allCodecoolers){
            codecoolerStrings.add(codecooler.toString());
        }

        mentorView.outputTable(codecoolerStrings);
    }

    public void displayQuests() throws SQLException {
        ArrayList<Quest> allQuests = questDAO.readAll();
        ArrayList<String> questStrings = new ArrayList<String>();

        for (Quest quest: allQuests){
            questStrings.add(quest.toString());
        }

        mentorView.outputTable(questStrings);
    }

    public void displayArtifacts() throws SQLException {
        ArrayList<Artifact> allArtifacts = artifactDAO.readAll();
        ArrayList<String> artifactStrings = new ArrayList<String>();

        for (Artifact artifact: allArtifacts){
            artifactStrings.add(artifact.toString());
        }

        mentorView.outputTable(artifactStrings);
    }

    public void displayTeams() throws SQLException{
        ArrayList<Team> allTeams = teamDAO.readAll();
        ArrayList<String> teamStrings = new ArrayList<String>();

        for (Team team: allTeams){
            teamStrings.add(team.toString());
        }

        mentorView.outputTable(teamStrings);
    }

    public void deleteQuest() throws SQLException{
        Quest chosenQuest = getQuestByName();

        if (chosenQuest != null){
            questDAO.delete(chosenQuest);
        }
    }

    public void deleteArtifact() throws SQLException{
        Artifact chosenArtifact = getArtifactByName();

        if (chosenArtifact != null){
            artifactDAO.delete(chosenArtifact);
        }
    }

    public void deleteTeam() throws SQLException{
        Team chosenTeam = getTeamByName();

        if (chosenTeam != null){
            teamDAO.delete(chosenTeam);
        }
    }

    public void markItem() throws SQLException{
        Codecooler chosenCodecooler = getCodecoolerByEmail();
        ArrayList<Item> codecoolerItems = itemDAO.getUserItems(chosenCodecooler.getWallet().getID());
        mentorView.displayUserItems(codecoolerItems);

        String chosenItemIndex = mentorView.getInput("Enter index of item: ");
        Item chosenItem = codecoolerItems.get(Integer.valueOf(chosenItemIndex));

        if (chosenItem.getMark()){
            mentorView.output("Item is already marked.");
        } else {
            chosenItem.setMark();
            itemDAO.update(chosenItem);
        }
    }

    public void displayWallet() throws SQLException {
        Codecooler chosenCodecooler = getCodecoolerByEmail();
        mentorView.output(chosenCodecooler.getWallet().toString());
    }

    public void createAchievement() throws SQLException {
        Codecooler chosenCodecooler = getCodecoolerByEmail();
        Quest chosenQuest = getQuestByName();
        Achievement newAchievement = new Achievement(chosenQuest, chosenCodecooler);
        achievementDAO.create(newAchievement);
    }

    public Codecooler getCodecoolerByEmail() throws SQLException{
        ArrayList<Codecooler> allCodecoolers = codecoolerDAO.readAll();
        String chosenCodecoolerEmail = mentorView.getRegExInput(mentorView.emailRegEx, "Enter email of a codecooler:");
        Codecooler chosenCodecooler = null;

        for (Codecooler codecooler : allCodecoolers) {
            if (chosenCodecoolerEmail.equals(codecooler.getEmail())) {
                chosenCodecooler = codecooler;
            }
        }
        return chosenCodecooler;
    }

    public Quest getQuestByName() throws SQLException{
        ArrayList<Quest> allQuests = questDAO.readAll();
        String chosenQuestName = mentorView.getInput("Enter name of a quest:");
        Quest chosenQuest = null;

        for (Quest quest : allQuests) {
            if (chosenQuestName.equals(quest.getName())) {
                chosenQuest = quest;
            }
        }
        return chosenQuest;
    }

    public Artifact getArtifactByName() throws SQLException{
        ArrayList<Artifact> allArtifacts = artifactDAO.readAll();
        String chosenArtifactName = mentorView.getInput("Enter name of an artifact: ");
        Artifact chosenArtifact = null;

        for (Artifact artifact: allArtifacts){
            if (chosenArtifactName.equals(artifact.getName())){
                chosenArtifact = artifact;
            }
        }
        return chosenArtifact;
    }

    public Team getTeamByName() throws SQLException{
        ArrayList<Team> allTeams = teamDAO.readAll();
        String chosenTeamName = mentorView.getInput("Enter name of a team: ");
        Team chosenTeam = null;

        for (Team team: allTeams){
            if (chosenTeamName.equals(team.getName())){
                chosenTeam = team;
            }
        }
        return chosenTeam;
    }
}