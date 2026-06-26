package com.aicode.dto;

/**
 * AI 模型排名条目
 */
public class AIRankingEntry {

    private int rank;
    private String modelName;
    private String provider;
    private double eloScore;
    private int votes;
    private String category;   // 类别：chat / coding / reasoning
    private String lastUpdated;

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public double getEloScore() { return eloScore; }
    public void setEloScore(double eloScore) { this.eloScore = eloScore; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
