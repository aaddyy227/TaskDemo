package com.example.demo.service;

import com.example.demo.dto.GameActivity;
import com.example.demo.dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GetLoserService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://challenge.dev.amusnetgaming.net";

    public Player getPlayerWithLargestGGR() {
        Player[] players = getPlayers();
        GameActivity[] activities = getGameActivities(players);
        return findPlayerWithLargestGGR(players, activities);
    }

    private Player[] getPlayers() {
        String url = BASE_URL + "/players?page=0&pageSize=30";
        return callWithTenRetries(url, Player[].class);
    }

    private GameActivity[] getGameActivities(Player[] players) {
        StringBuilder url = new StringBuilder(BASE_URL + "/game-activity?");
        for (Player player : players) {
            url.append("playerIds=").append(player.getId()).append("&");
        }
        url.append("page=0&pageSize=600");
        return callWithTenRetries(url.toString(), GameActivity[].class);
    }

    private <T> T callWithTenRetries(String url, Class<T> responseType) {
        RestClientException lastException = null;
        for (int i = 0; i < 10; i++) {
            try {
                return restTemplate.getForObject(url, responseType);
            } catch (RestClientException e) {
                lastException = e;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException("API call failed after 10 attempts", lastException);
    }

    private Player findPlayerWithLargestGGR(Player[] players, GameActivity[] activities) {
        Map<Integer, Double> playerGGR = new HashMap<>();

        for (GameActivity activity : activities) {
            int playerId = activity.getPlayerId();
            double ggr = activity.getBetAmount() - activity.getWinAmount();
            if (playerGGR.containsKey(playerId)) {
                playerGGR.put(playerId, playerGGR.get(playerId) + ggr);
            } else {
                playerGGR.put(playerId, ggr);
            }
        }

        Player loser = null;
        double maxGGR = Double.MIN_VALUE;

        for (Player player : players) {
            Double ggr = playerGGR.get(player.getId());
            if (ggr != null && ggr > maxGGR) {
                maxGGR = ggr;
                loser = player;
            }
        }

        return loser;
    }
}