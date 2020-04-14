package client;

import com.google.gson.Gson;
import domain.Command;
import domain.decoder.CommandDecoder;
import domain.encoder.CommandEncoder;
import service.ServerEndpointService;
import service.impl.ServerEndpointServiceImpl;

import javax.websocket.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

@javax.websocket.ClientEndpoint(decoders = CommandDecoder.class, encoders = CommandEncoder.class)
public class ClientGameEndpoint {
    private static Gson gson = new Gson();
    private Logger logger = Logger.getLogger(this.getClass().getName());
    Scanner scanner = new Scanner(System.in);
    int missions = 1;

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {

        System.out.println("Prikacio si se ");

    }

    @OnMessage
    public void onMessage(Command command, Session session) throws IOException {

        switch (command.getCommand()) {
            case "role":
                System.out.println("Your roll is " + command.getValue());

                System.out.println("Your against ");
                for (String s:command.getNominated()) {
                    System.out.println(s);
                }

                break;

            case "onMove":
                System.out.println("Na potezu je " + command.getValue());
                if (command.getValue().equals(session.getUserProperties().get("username"))) {
                    System.out.println("Ti si na potezu");
                    nominatedPlayersToMission(5,session);

                }
                break;

            case "nominated":

                String []nominated=command.getNominated();
                    System.out.println("Nominovani su : ");
                for (String s:nominated) {
                    System.out.println(s);

                }
                System.out.println("Zelite li da nominovani podju na misiju? Ako zelite, napisite 1 a 0 u suprotnom");
              vote(session,"voteForMission");


                break;

            case "nominatedVote":

                  showNominated(command.getNominated(),command.getVotes());


                break;

            case "missionStarted":

                if(areYouNominated(command.getNominated(),session)){

                    System.out.println("Zelite li da misija prodje ili padne? Ako zelite, napisite 1 a 0 u suprotnom");
                        vote(session,"voteInMission");

                }
                missions++;


                break;


            case "missionFinished":

                System.out.println("Mission has"+command.getValue()+"\nNumber of negative votes are "+command.getNumberOfNegativeVotes());

                break;

            case "gameOver":
                    System.out.println("Victory goes to "+command.getValue());

                    break;





        }

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        logger.info("Ne treba da se zatvori");
    }


    public void nominatedPlayersToMission( int numberOfPlayers,Session session) {
        System.out.println("Misija je"+missions);

        messageClient(numberOfPlayers,session);


    }

    public void messageClient( int numberOfPlayers, Session session) {
        Command command = null;

        //Dvoje saljem
        if ((missions==1 && numberOfPlayers>=5 && numberOfPlayers<=7) || (missions==3 && numberOfPlayers==5)) {
            System.out.println("Izaberite prvog igraca");
            String playerSend1 = scanner.nextLine();
            System.out.println("Izaberite drugog igraca");
            String playerSend2 = scanner.nextLine();
            command = new Command("nominated", null, playerSend1, playerSend2);

        }

        //Troje saljem

      if(((missions==2 || missions>=4) && numberOfPlayers==5)||((missions==2 || missions==4) && numberOfPlayers==6)
      || (numberOfPlayers==7 && missions==2|| missions==3) || (missions==1 && numberOfPlayers>=8)) {
            System.out.println("Izaberite prvog igraca");
            String playerSend1=scanner.nextLine();
            System.out.println("Izaberite drugog igraca");
            String playerSend2=scanner.nextLine();
            System.out.println("Izaberite treceg igraca ");
            String playerSend3=scanner.nextLine();
            command=new Command("nominated",null,playerSend1,playerSend2,playerSend3);


        }

        //Cetvoro
/*
        if(){


            if(){
                System.out.println("Izaberite prvog igraca");
                String playerSend1=scanner.nextLine();
                System.out.println("Izaberite drugog igraca");
                String playerSend2=scanner.nextLine();
                System.out.println("Izaberite treceg igraca ");
                String playerSend3=scanner.nextLine();
                System.out.println("Izaberite cetvrtog igraca ");
                String playerSend4=scanner.nextLine();
                command=new Command("nominated",null,playerSend1,playerSend2,playerSend3,playerSend4);


            }


            if(){
                System.out.println("Izaberite prvog igraca");
                String playerSend1=scanner.nextLine();
                System.out.println("Izaberite drugog igraca");
                String playerSend2=scanner.nextLine();
                System.out.println("Izaberite treceg igraca ");
                String playerSend3=scanner.nextLine();
                System.out.println("Izaberite cetvrtog igraca ");
                String playerSend4=scanner.nextLine();
                System.out.println("Izaberite petog igraca ");
                String playerSend5=scanner.nextLine();
                command=new Command("nominated",null,playerSend1,playerSend2,playerSend3,playerSend4,playerSend5);


            }*/
        try {
            session.getBasicRemote().sendObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }


    }

    public void showNominated(String[] nameVotes,Boolean[] votes){

        for (int i = 0; i <nameVotes.length ; i++) {
            System.out.println("Player "+nameVotes[i]+" voted "+votes[i]);
        }

    }
    public boolean areYouNominated(String[] nameVotes,Session  session){
        System.out.println("NOMINATED METOD");

        for (int i = 0; i <nameVotes.length ; i++) {
            if(nameVotes[i].equals(session.getUserProperties().get("username")))
                return true;
        }
            return false;
    }

    public void vote(Session session,String value){

        Command command1=null;


        int odgovor=Integer.parseInt(scanner.nextLine());
        if(odgovor==1) {
            command1 = new Command(value, true);
        }
        else{
            command1=new Command(value,false);
        }
        try {
            session.getBasicRemote().sendObject(command1);
        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        }
    }

}
