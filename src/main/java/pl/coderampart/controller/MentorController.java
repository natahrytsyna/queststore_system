package pl.coderampart.controller;

import pl.coderampart.DAO.*;
import pl.coderampart.model.*;
import pl.coderampart.services.Bootable;
import pl.coderampart.view.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MentorController implements Bootable<Mentor> {

    private Mentor selfMentor;
    private MentorView mentorView = new MentorView();
    private CodecoolerDAO codecoolerDAO = new CodecoolerDAO();
    private GroupDAO groupDAO = new GroupDAO();
    private TeamDAO teamDAO = new TeamDAO();
    private ArtifactDAO artifactDAO = new ArtifactDAO();
    private ItemDAO itemDAO = new ItemDAO();
    private WalletDAO walletDAO = new WalletDAO();
    private QuestDAO questDAO = new QuestDAO();

    private static final int CREATE_CODECOOLER = 1;
    private static final int CREATE_QUEST = 2;
    private static final int CREATE_ARTIFACT = 3;
    private static final int CREATE_TEAM = 4;
    private static final int EDIT_CODECOOLER = 5;
    private static final int EDIT_QUEST = 6;
    private static final int EDIT_ARTIFACT = 7;
    private static final int EDIT_TEAM = 8;
    private static final int DISPLAY_CODECOOLERS = 9;
    private static final int DISPLAY_QUESTS = 10;
    private static final int DISPLAY_ARTIFACTS = 11;
    private static final int DISPLAY_TEAMS = 12;
    private static final int DELETE_QUEST = 13;
    private static final int DELETE_ARTIFACT = 14;
    private static final int DELETE_TEAM = 15;
    private static final int EXIT = 0;

    public boolean start(Mentor mentor) {
        mentorView.displayMentorMenu();
        selfMentor = mentor;

        int userChoice = mentorView.getUserChoice();

        mentorView.clearTerminal();
        // TODO: USE ENUM, DECLARE SUBMENUS FOR EACH CLASS
        switch(userChoice) {

            case CREATE_CODECOOLER: createCodecooler();
                break;
            case CREATE_QUEST: createQuest();
                break;
            case CREATE_ARTIFACT: createArtifact();
                break;
            case CREATE_TEAM: createTeam();
                break;
            case EDIT_CODECOOLER: editCodecooler();
                break;
            case EDIT_QUEST: editQuest();
                break;
            case EDIT_ARTIFACT: editArtifact();
                break;
            case EDIT_TEAM: editTeam();
                break;
            case DISPLAY_CODECOOLERS: displayCodecoolers();
                break;
            case DISPLAY_QUESTS: displayQuests();
                break;
            case DISPLAY_ARTIFACTS: displayArtifacts();
                break;
            case DISPLAY_TEAMS: displayTeams();
                break;
            case DELETE_QUEST: deleteQuest();
                break;
            case DELETE_ARTIFACT: deleteArtifact();
                break;
            case DELETE_TEAM: deleteTeam();
                break;
            case EXIT:
                return false;
        }

        mentorView.enterToContinue();
        return true;
    }

    public void createCodecooler(){
        this.displayCodecoolers();

        String[] codecoolerData = mentorView.getUserData();

        Codecooler newCodecooler = new Codecooler(codecoolerData[0], codecoolerData[1], mentorView.stringToDate(codecoolerData[2]),
                                                  codecoolerData[3], codecoolerData[4]);

        this.displayTeams();
        ArrayList<Team> allTeams = teamDAO.readAll();
        String chosenTeamName = mentorView.getInput("Enter name of a team you wish to assign this Codecooler to, " +
                                              "\nAdditionally, Codecooler will be assigned to the group of this mentor");
        newCodecooler.setGroup(selfMentor.getGroup());

        codecoolerDAO.create(newCodecooler);
    }

    public void createQuest(){
        this.displayQuests();

        String[] questData = mentorView.getQuestData();

        Quest newQuest = new Quest(questData[0], questData[1], Integer.valueOf(questData[2]));

        questDAO.create(newQuest);
    }

    public void createArtifact(){
        this.displayArtifacts();

        String[] artifactData = mentorView.getArtifactData();

        Artifact newArtifact = new Artifact(artifactData[0], artifactData[1], artifactData[2], Integer.valueOf(artifactData[3]));

        artifactDAO.create(newArtifact);
    }

    public void createTeam(){
        this.displayTeams();

        String teamName = mentorView.getInput("Enter name of a new team: ");

        Team newTeam = new Team(teamName, selfMentor.getGroup());

        teamDAO.create(newTeam);
    }

    public void editCodecooler(){

        Codecooler changedCodecooler = null;

        ArrayList<Codecooler> allCodecoolers = codecoolerDAO.readAll();
        String chosenCodecoolerEmail = mentorView.getInput("Enter email of a codecooler you wish to edit: ");

        for (Codecooler codecooler: allCodecoolers){
            if (chosenCodecoolerEmail.equals(codecooler.getEmail())){
                changedCodecooler = codecooler;
                break;
            }
        }

        if (changedCodecooler != null) {
            final int EDIT_FIRST = 1;
            final int EDIT_LAST = 2;
            final int EDIT_EMAIL = 3;
            final int EDIT_PASSWORD = 4;
            final int EDIT_BIRTHDATE = 5;

            ArrayList<String> editCodecoolerOptions = new ArrayList<>(Arrays.asList("Edit first name", "Edit last name,",
                    "Edit email", "Edit password",
                    "Edit birthdate"));
            mentorView.displayOptions(editCodecoolerOptions);
            int userChoice = mentorView.getUserChoice();
            mentorView.clearTerminal();

            switch(userChoice){
                case EDIT_FIRST:
                    changedCodecooler.setFirstName(mentorView.getInput("Enter new name: "));
                    break;
                case EDIT_LAST:
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

    public void editQuest(){

        Quest changedQuest = null;

        ArrayList<Quest> allQuests = questDAO.readAll();
        String chosenQuestName = mentorView.getInput("Enter name of a quest you wish to edit: ");
        for (Quest quest: allQuests){
            if(chosenQuestName.equals(quest.getName())){
                changedQuest = quest;
                break;
            }
        }

        if (changedQuest != null){

            final int EDIT_NAME = 1;
            final int EDIT_DESCR = 2;
            final int EDIT_REWARD = 3;

            ArrayList<String> editQuestOptions = new ArrayList<>(Arrays.asList("Edit name: ", "Edit description: ",
                                                                               "Edit reward: "));
            mentorView.displayOptions(editQuestOptions);
            int userChoice = mentorView.getUserChoice();
            mentorView.clearTerminal();

            switch(userChoice){
                case EDIT_NAME:
                    changedQuest.setName(mentorView.getInput("Enter new name: "));
                    break;
                case EDIT_DESCR:
                    changedQuest.setDescription(mentorView.getInput("Enter new description: "));
                    break;
                case EDIT_REWARD:
                    changedQuest.setReward(Integer.valueOf(mentorView.getInput("Enter new reward: ")));
                    break;
            }

            questDAO.update(changedQuest);
        }
    }

    public void editArtifact(){

    }

    public void editTeam(){
        this.displayTeams();

        Team changedTeam = null;

        ArrayList<Team> allTeams = teamDAO.readAll();
        String chosenTeamName = mentorView.getInput("Enter name of a group you wish to edit: ");

        for (Team team: allTeams){
            if (chosenTeamName.equals(team.getName())){
                changedTeam = team;
            }
        }

        if(!changedTeam.equals(null)){
            changedTeam.setName(teamView.getInput("Enter new name: "));
        }

        teamDAO.update(changedTeam);
    }

    public void displayCodecoolers(){
        ArrayList<Codecooler> allCodecoolers = codecoolerDAO.readAll();
        ArrayList<String> codecoolerStrings = new ArrayList<String>();

        for (Codecooler codecooler: allCodecoolers){
            codecoolerStrings.add(codecooler.toString());
        }

        mentorView.outputTable(codecoolerStrings);
    }

    public void displayQuests(){
        ArrayList<Quest> allQuests = questDAO.readAll();
        ArrayList<String> questStrings = new ArrayList<String>();

        for (Quest quest: allQuests){
            questStrings.add(quest.toString());
        }

        mentorView.outputTable(questStrings);
    }

    public void displayArtifacts(){
        ArrayList<Artifact> allArtifacts = artifactDAO.readAll();
        ArrayList<String> artifactStrings = new ArrayList<String>();

        for (Artifact artifact: allArtifacts){
            artifactStrings.add(artifact.toString());
        }

        mentorView.outputTable(artifactStrings);
    }

    public void displayTeams(){
        ArrayList<Team> allTeams = teamDAO.readAll();
        ArrayList<String> teamStrings = new ArrayList<String>();

        for (Team team: allTeams){
            teamStrings.add(team.toString());
        }

        mentorView.outputTable(teamStrings);
    }

    public void deleteQuest(){
        this.displayQuests();

        ArrayList<Quest> allQuests = questDAO.readAll();
        String chosenQuestName = mentorView.getInput("Enter name of a quest you wish to delete: ");

        for (Quest quest: allQuests){
            if (chosenQuestName.equals(quest.getName())){
                questDAO.delete(quest);
            }
        }
    }

    public void deleteArtifact(){
        this.displayArtifacts();

        ArrayList<Artifact> allArtifacts = artifactDAO.readAll();
        String chosenArtifactName = mentorView.getInput("Enter name of an artifact you wish to delete: ");

        for (Artifact artifact: allArtifacts){
            if (chosenArtifactName.equals(artifact.getName())){
                artifactDAO.delete(artifact);
            }
        }
    }

    public void deleteTeam() {
        this.displayTeams();

        ArrayList<Team> allTeams = teamDAO.readAll();
        String chosenTeamName = mentorView.getInput("Enter name of a team you wish to delete: ");

        for (Team team: allTeams){
            if (chosenTeamName.equals(team.getName())){
                teamDAO.delete(team);
            }
        }
    }
}