package endpoint;

import domain.Command;
import domain.Room;
import domain.decoder.CommandDecoder;
import domain.decoder.PlayerDecoder;
import domain.encoder.CommandEncoder;
import domain.encoder.PlayerEncoder;
import server.GameThread;
import service.GameEndpointService;
import service.RoomEndpointService;
import service.ServerEndpointService;

import service.impl.RoomEndpointServiceImpl;
import service.impl.ServerEndpointServiceImpl;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

@ServerEndpoint(value = "/Server/{roomId}/{playerId}/Game", decoders = CommandDecoder.class, encoders = CommandEncoder.class)
public class GameEndpoint {


    private Logger logger = Logger.getLogger(this.getClass().getName());
    public ServerEndpointService serverEndpointService= new ServerEndpointServiceImpl();
    public static Set<Room> rooms = endpoint.ServerEndpoint.rooms;
    public static boolean wait=true;
    public static boolean newSessionOver = false;

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("playerId") String playerId) throws IOException, InterruptedException {
//        logger.info("Connected in room: " + roomId);

        logger.info("Zahtev od " + playerId + " sessionId " + session.getId() +
                "\nVreme " + java.lang.System.currentTimeMillis());

        newSession(roomId,playerId,session);
        while(!newSessionOver)
        {
            Thread.sleep(1000);
        }
        newSessionOver = false;
        if(allConnected(serverEndpointService.findRoom(roomId,rooms).getPlayers())){
            Thread.sleep(500);
            logger.info("SIBAJ DALJE!");
            GameThread.playersConnected = true;
        }


    }


    @OnMessage
    public void onMessage(Command command, Session session, @PathParam("roomId") String roomId, @PathParam("playerId") String playerId) {

        switch(command.getCommand()){
            case "nominated":
                serverEndpointService.findRoom(roomId,rooms).setNominated(command.getNominated());

                sendNominationToAllPlayers(command.getNominated(),roomId);
                serverEndpointService.findRoom(roomId,rooms).voteInMission.setPlayerInMission(command.getNominated().length);
                serverEndpointService.findRoom(roomId,rooms).voteForMission.setNominated(command.getNominated());
                serverEndpointService.findRoom(roomId,rooms).voteInMission.setNominated(command.getNominated());


                //  serverEndpointService.findRoom(roomId,rooms).setOnMovePlayer(true);

                break;

            case "voteForMission":

                serverEndpointService.findRoom(roomId,rooms).voteForMission.setVotes(command.isAccepted());
                serverEndpointService.findRoom(roomId,rooms).voteForMission.setVoteNames(session.getUserProperties().get("username").toString());
                int valueForVote=serverEndpointService.findRoom(roomId,rooms).voteForMission.setVotesNumberAndOthers();

                if(valueForVote==1){
                    serverEndpointService.findRoom(roomId,rooms).setOnMovePlayer(true);
                }
                System.out.println(valueForVote);

                break;

            case "voteInMission":

                serverEndpointService.findRoom(roomId,rooms).voteInMission.setVotes(command.isAccepted());
                serverEndpointService.findRoom(roomId,rooms).voteInMission.setVoteNames(session.getUserProperties().get("username").toString());
                int valueForMission=serverEndpointService.findRoom(roomId,rooms).voteInMission.setVotesNumberAndOthers();
                if(valueForMission==0){
                    if(serverEndpointService.findRoom(roomId,rooms).voteInMission.getQuest()>=3 && serverEndpointService.findRoom(roomId,rooms).voteInMission.getGood()==3){
                        Command command1=new Command("gameOver","Good");
                        serverEndpointService.findRoom(roomId,rooms).voteInMission.sendVotes(command1);
                        break;
                    }
                    if(serverEndpointService.findRoom(roomId,rooms).voteInMission.getQuest()>=3 && serverEndpointService.findRoom(roomId,rooms).voteInMission.getEvil()==3){
                        Command command1=new Command("gameOver","Evil");
                        serverEndpointService.findRoom(roomId,rooms).voteInMission.sendVotes(command1);
                        break;
                    }
                    serverEndpointService.findRoom(roomId,rooms).setOnMovePlayer(true);

                }
                break;

            case"guessMerlin":

                //Trebam da proverim da li ovaj userName ima lika Merlina!
                String username=command.getValue();
                String []assassinAndMerlin=new String[2];
                assassinAndMerlin[0]=session.getUserProperties().get("username").toString();
                assassinAndMerlin[1]=findMerlin(roomId);
                try {
                    Session player=findPlayer(username,roomId);
                    if(isPlayerMerlin(player)){

                        Command command1=new Command("merlinGuessed",assassinAndMerlin,true);
                        serverEndpointService.findRoom(roomId,rooms).voteForMission.sendVotes(command1);

                    }
                    else {
                        Command command1 = new Command("merlinGuessed", assassinAndMerlin,false);
                        serverEndpointService.findRoom(roomId, rooms).voteForMission.sendVotes(command1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }

    public boolean allConnected(Set<Session> players){
        for (Iterator<Session> it = players.iterator(); it.hasNext(); ) {
            Session s = it.next();
            if(s.getUserProperties().get("connected").equals("false")){
                return false;
            }
        }
        return true;
    }

    public void newSession(String roomId, String playerId,Session session) {
        Room room = serverEndpointService.findRoom(roomId,rooms);
        if(room == null)
            System.out.println("mrtvi room null");
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();

//            System.out.println(s.getUserProperties().get("playerId") + " igrac");
            if (s.getUserProperties().get("playerId").equals(playerId)){
                System.out.println("----------------------------\n" +
                        "Menjam session " + playerId + " username " + s.getUserProperties().get("username"));
                String username=s.getUserProperties().get("username").toString();

                session.getUserProperties().put("username",username);
                session.getUserProperties().put("roomId",roomId);
                session.getUserProperties().put("playerId",playerId);
                session.getUserProperties().put("connected","true");
                room.getPlayers().remove(s);
                room.getPlayers().add(session);

                System.out.println("Promenio session " + playerId);
                newSessionOver = true;
                return;
            }
        }
    }

    public void sendNominationToAllPlayers(String[] nominated,String roomId){
        Room room = serverEndpointService.findRoom(roomId,rooms);
        Command command=new Command("nominated",null,nominated);
        if(room == null)
            System.out.println("mrtvi room null");
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();
            try {
                s.getBasicRemote().sendObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }

        }

    }

    public Session findPlayer(String username,String rommId) throws Exception {
        Room room=serverEndpointService.findRoom(rommId,rooms);


        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
                Session s=it.next();
                if(s.getUserProperties().get("username").equals(username))
                    return s;

        }
        throw new Exception("There is no player with username: "+username+" in room");
    }
    public boolean isPlayerMerlin(Session session){
        return (session.getUserProperties().get("role").equals("Merlin"))?true:false;
    }
    public String findMerlin(String rommId){
        Room room=serverEndpointService.findRoom(rommId,rooms);
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s=it.next();
            if(s.getUserProperties().get("username").equals("Merlin"))
                return s.getUserProperties().get("username").toString();

        }
        try {
            throw new Exception("There is no Merlin here");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

