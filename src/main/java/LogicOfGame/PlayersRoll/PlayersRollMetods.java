package LogicOfGame.PlayersRoll;


import domain.Command;
import domain.Room;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class PlayersRollMetods {

    boolean izmenjeno = false;

    public void setPlayersRoll(Room room) throws IOException, EncodeException {
        int frequency=1;
        LinkedList<Session> listOfCharacters=new LinkedList<Session>();
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();
            giveANameToPlayers(frequency,s,room.getNumberOfPlayers(),listOfCharacters);
            frequency++;


        }

        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();
            sendToServerRolls(s,listOfCharacters);

        }
    }

    private void sendToServerRolls(Session session,LinkedList<Session> listOfCharacters) throws IOException, EncodeException {
        Command command =null;
        if(session.getUserProperties().get("role").equals("Merlin")){

            if(listOfCharacters.size()==5 || listOfCharacters.size()==6){
                String Morgana=session.getUserProperties().get("Morgana").toString();
                String Assassin=session.getUserProperties().get("Assassin").toString();
                String Roll=session.getUserProperties().get("role").toString();
                System.out.println("-\n" +
                        "Ti si sabane " + session.getUserProperties().get("username") +
                        "Tvoja uloga je " + Roll +
                        "Ti znas za " + Morgana+ ", " + Assassin +
                        "\n-");
                command = new Command("role",Roll,Morgana,Assassin);
            }
            if(listOfCharacters.size()==7 || listOfCharacters.size()==10){
                String Morgana=session.getUserProperties().get("Morgana").toString();
                String Assassin=session.getUserProperties().get("Assassin").toString();
                String Oberon=session.getUserProperties().get("Oberon").toString();
                String Roll=session.getUserProperties().get("role").toString();
                command = new Command("role",Roll,Morgana,Assassin,Oberon);
            }

            if(listOfCharacters.size()==8){
                String Morgana=session.getUserProperties().get("Morgana").toString();
                String Assassin=session.getUserProperties().get("Assassin").toString();
                String Mordred=session.getUserProperties().get("Mordred").toString();

                String Roll=session.getUserProperties().get("role").toString();
                command = new Command("role",Roll,Morgana,Assassin,Mordred);
            }

            if(listOfCharacters.size()==9){
                String Morgana=session.getUserProperties().get("Morgana").toString();
                String Assassin=session.getUserProperties().get("Assassin").toString();
                String Mordred=session.getUserProperties().get("Mordred").toString();
                String Oberon=session.getUserProperties().get("Oberon").toString();

                String Roll=session.getUserProperties().get("role").toString();
                command = new Command("role",Roll,Morgana,Assassin,Mordred,Oberon);
            }


        }

        if(session.getUserProperties().get("role").equals("Morgana")){

            if(listOfCharacters.size()>=5 && listOfCharacters.size()<=7) {

                String Assassin=session.getUserProperties().get("Assassin").toString();
                String Roll=session.getUserProperties().get("role").toString();
                System.out.println("-\n" +
                        "Ti si sabane " + session.getUserProperties().get("username") +
                        "\nTvoja uloga je " + Roll +
                        "\nTi znas za " + Assassin +
                        "\n-");
                command = new Command("role",Roll,Assassin);
            }

            if(listOfCharacters.size()>=8 && listOfCharacters.size()<=10){

                String Assassin=session.getUserProperties().get("Assassin").toString();
                String Mordred=session.getUserProperties().get("Mordred").toString();

                String Roll=session.getUserProperties().get("role").toString();
                command = new Command("role",Roll,Assassin,Mordred);
            }

        }

        if(session.getUserProperties().get("role").equals("Percival")){
            String Merlin=session.getUserProperties().get("Merlin").toString();
            String Morgana=session.getUserProperties().get("Morgana").toString();
            String Roll=session.getUserProperties().get("role").toString();
            if(randomMorganaMerlin()==0)
                command = new Command("role",Roll,Merlin,Morgana);
            else{
                command=new Command("role",Roll,Morgana,Merlin);
            }

        }

        if(session.getUserProperties().get("role").equals("Assassin")){

            if(listOfCharacters.size()>=5 && listOfCharacters.size()<=7) {

                String Morgana=session.getUserProperties().get("Morgana").toString();
                String Roll=session.getUserProperties().get("role").toString();
                command=new Command("role",Roll,Morgana);
            }

            if(listOfCharacters.size()>=8 && listOfCharacters.size()<=10){

                String Morgana=session.getUserProperties().get("Morgana").toString();
                String Mordred=session.getUserProperties().get("Mordred").toString();

                String Roll=session.getUserProperties().get("role").toString();
                command=new Command("role",Roll,Morgana,Mordred);
            }
        }

        if(session.getUserProperties().get("role").equals("Mordred")){

            String Morgana=session.getUserProperties().get("Morgana").toString();
            String Assassin=session.getUserProperties().get("Assassin").toString();

            String Roll=session.getUserProperties().get("role").toString();
            command = new Command("role",Roll,Morgana,Assassin);
        }

                if(command==null){
                    String Roll=session.getUserProperties().get("role").toString();
                    command=new Command("role",Roll);
                }

       
         
        

        System.out.println("Igracu saljem " + session.getUserProperties().get("username") + " Command: " + command);
        session.getBasicRemote().sendObject(command);

    }

    private void  giveANameToPlayers(int frequency, Session session, int numberOfPlayers, LinkedList<Session> listOfCharacters){

        switch(frequency){

            case 1:
                session.getUserProperties().put("role", CharactersName.Merlin.toString());
                listOfCharacters.add(0,session);


                break;
            case 2:
                session.getUserProperties().put("role", CharactersName.Morgana.toString());
                listOfCharacters.add(1,session);
                //Ova metoda funkcionise tako sto prima prvi parametar koji predstavlja index onome kome zelimo da nalepimo username igraca kojeg njegov lik zna
                // u ovom slucaju zelim da nalepim Merlinu koji je uvijek index 0 . U Session.getUserProperty().put sam stavio kljuc ime igraca
                //treci parametar prosledjujem session igraca kome zelimo da uzmemo username i da prilepimo nasem igracu
                addSpecialPlayer(0,"Morgana",listOfCharacters.get(1),listOfCharacters);
                break;
            case 3:
                session.getUserProperties().put("role", CharactersName.Percival.toString());
                listOfCharacters.add(2,session);
                addSpecialPlayer(2,"Morgana",listOfCharacters.get(1),listOfCharacters);
                addSpecialPlayer(2,"Merlin",listOfCharacters.get(0),listOfCharacters);
                break;
            case 4:
                session.getUserProperties().put("role", CharactersName.Assassin.toString());
                listOfCharacters.add(3,session);
                addSpecialPlayer(3,"Morgana",listOfCharacters.get(1),listOfCharacters);
                addSpecialPlayer(1,"Assassin",listOfCharacters.get(3),listOfCharacters);
                addSpecialPlayer(0,"Assassin",listOfCharacters.get(3),listOfCharacters);
                break;
            case 5:
                session.getUserProperties().put("role", CharactersName.Pleb1.toString());
                listOfCharacters.add(4,session);
                break;
            case 6:
                session.getUserProperties().put("role", CharactersName.Pleb2.toString());
                listOfCharacters.add(5,session);
                break;
            case 7:
                if(numberOfPlayers==7){
                    session.getUserProperties().put("role", CharactersName.Oberon.toString());
                    listOfCharacters.add(6,session);
                    addSpecialPlayer(0,"Oberon",listOfCharacters.get(6),listOfCharacters);
                }

                else{
                    session.getUserProperties().put("role", CharactersName.Pleb3.toString());
                    listOfCharacters.add(6,session);
                }

                break;
            case 8:
                session.getUserProperties().put("role", CharactersName.Mordred.toString());
                listOfCharacters.add(7,session);
                addSpecialPlayer(1,"Mordred",listOfCharacters.get(7),listOfCharacters);
                addSpecialPlayer(3,"Mordred",listOfCharacters.get(7),listOfCharacters);
                addSpecialPlayer(7,"Morgana",listOfCharacters.get(1),listOfCharacters);
                addSpecialPlayer(7,"Assassin",listOfCharacters.get(3),listOfCharacters);

                break;
            case 9:
                session.getUserProperties().put("role", CharactersName.Oberon.toString());
                listOfCharacters.add(8,session);
                addSpecialPlayer(0,"Oberon",listOfCharacters.get(8),listOfCharacters);
                //Ako ima 9 igraca, znaci da Merlin zna za Mordreda
                if(numberOfPlayers==9)
                    addSpecialPlayer(0,"Mordred",listOfCharacters.get(7),listOfCharacters);
                break;
            case 10:


                session.getUserProperties().put("role", CharactersName.Lancelot.toString());
                listOfCharacters.add(9,session);
                break;

        }

    }

    private void addSpecialPlayer(int index,String name,Session session,LinkedList<Session> listOfCharacters){
        listOfCharacters.get(index).getUserProperties().put(name,session.getUserProperties().get("username"));

    }

    private int randomMorganaMerlin(){
        Random random=new Random();
        return random.nextInt(2);
    }
}
