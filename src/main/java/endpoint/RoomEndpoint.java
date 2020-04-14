package endpoint;

import domain.decoder.PlayerDecoder;
import domain.encoder.PlayerEncoder;
import domain.Room;
import service.RoomEndpointService;
import service.ServerEndpointService;
import service.impl.RoomEndpointServiceImpl;
import service.impl.ServerEndpointServiceImpl;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;

import java.util.Set;
import java.util.logging.Logger;

@javax.websocket.server.ServerEndpoint(value = "/Server/{roomId}/{playerId}", decoders = PlayerDecoder.class, encoders = PlayerEncoder.class)
public class RoomEndpoint {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    public RoomEndpointService roomEndpointService = new RoomEndpointServiceImpl();
    public ServerEndpointService serverEndpointService=new ServerEndpointServiceImpl();
    static boolean firstPlayer=false;
    static int numberOfPlayers=0;
    public static Set<Room> rooms = ServerEndpoint.rooms;

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("playerId") String playerId) throws IOException {
//        logger.info("Connected in room: " + roomId);

        if(firstPlayer==false){
            rooms.add(new Room("1"));
            firstPlayer=true;
        }

        session.getUserProperties().put("roomId",roomId);
        session.getUserProperties().put("playerId",playerId);
        session.getUserProperties().put("username","Null");
        Room room=serverEndpointService.findRoom("1",rooms);

        room.addPlayer(session);
        session.getBasicRemote().sendText("You entered in a room");
        numberOfPlayers++;
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId, @PathParam("playerId") String playerId) {
        System.out.println("Username " + message);
       /* switch (message) {
            case "startGame":
                int numberOfPlayerinRoom = serverEndpointService.findRoom(roomId, rooms).getPlayers().size();
                serverEndpointService.findRoom(roomId, rooms).setNumberOfPlayers(numberOfPlayerinRoom);
                roomEndpointService.startGame(roomId, rooms);
                break;
            default:
                roomEndpointService.newSession(message, roomId, playerId, rooms, session);
                String playerInRoom = roomEndpointService.playersInRoom(roomId, playerId, rooms, session);
                System.out.println("Players in room " + roomId + ": " + playerInRoom);
                roomEndpointService.sendToAll(playerInRoom, roomId, rooms);
                break;
        }*/

        switch (message) {
            case "startGame":
                //Ovo sluzi da mi da podatke o broju igraca !
                int numberOfPlayerinRoom = serverEndpointService.findRoom(roomId, rooms).getPlayers().size();
                serverEndpointService.findRoom(roomId, rooms).setNumberOfPlayers(numberOfPlayerinRoom);
                roomEndpointService.startGame(roomId, rooms);
                break;

            default:

                roomEndpointService.newSession(message, roomId, playerId, rooms, session);

                String playerInRoom = roomEndpointService.playersInRoom(roomId, playerId, rooms, session);


                roomEndpointService.sendToAll(playerInRoom, roomId, rooms);
//               System.out.println("Broj igraca je "+numberOfPlayers);
                if (numberOfPlayers == 5) {
                    int numberOfPlayerinRoom2 = serverEndpointService.findRoom(roomId, rooms).getPlayers().size();
                    serverEndpointService.findRoom(roomId, rooms).setNumberOfPlayers(numberOfPlayerinRoom2);
//                    System.out.println("Broj igraca je "+ serverEndpointService.findRoom(roomId,rooms).getNumberOfPlayers());
                    roomEndpointService.startGame(roomId, rooms);
                }


        }


    }
     @OnClose
    public void onClose(Session session, CloseReason closeReason) {
//        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }

}
