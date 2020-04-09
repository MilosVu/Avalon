package service.impl;

import domain.Player;
import domain.Room;
import endpoint.ServerEndpoint;
import server.GameThread;
import service.RoomEndpointService;
import service.ServerEndpointService;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

public class RoomEndpointServiceImpl implements RoomEndpointService {

    public ServerEndpointService serverEndpointService = new ServerEndpointServiceImpl();

    @Override
    public String playersInRoom(String roomId, String playerId, Set<Room> rooms, Session session) {
        Room room = serverEndpointService.findRoom(roomId,rooms);
        String playersInRoom = "";
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();
            if(!s.getUserProperties().get("username").equals("null")) {
                playersInRoom += s.getUserProperties().get("username") + "," ;
            }
        }
        return playersInRoom;
    }

    @Override
    public void newSession(String message, String roomId, String playerId, Set<Room> rooms, Session session) {
        Room room = serverEndpointService.findRoom(roomId,rooms);
        if(room == null)
            System.out.println("mrtvi room null");
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();
            System.out.println(s.getUserProperties().get("playerId") + " igrac");
            if (s.getUserProperties().get("playerId").equals(playerId)){
                System.out.println("Stavljam username " + message);
                session.getUserProperties().put("username",message);
                session.getUserProperties().put("roomId",roomId);
                session.getUserProperties().put("playerId",playerId);
                room.getPlayers().remove(s);
                room.getPlayers().add(session);

                return;
            }
        }
    }

    @Override
    public Session findPlayer(String roomId, String playerId, Set<Room> rooms) {
        return null;
    }

    @Override
    public void sendToAll(String message, String roomId, Set<Room> rooms) {
        Room room = serverEndpointService.findRoom(roomId,rooms);
        for (Iterator<Session> it = room.getPlayers().iterator(); it.hasNext(); ) {
            Session s = it.next();
            if(!s.getUserProperties().get("username").equals("null")) {
                try {
                    System.out.println(s.getUserProperties().get("username") + " saljem " + message);
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void startGame(String roomId,Set<Room> rooms) {
        System.out.println("START GAME USAOOOO");
        sendToAll("startGame",roomId,rooms);
        GameThread gameThread = new GameThread(serverEndpointService.findRoom(roomId,rooms));
        gameThread.start();
    }
}
