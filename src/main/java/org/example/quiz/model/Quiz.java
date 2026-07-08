package org.example.quiz.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Quiz model. Table owned by Part B; C reads it for the taking-flow. */
public class Quiz {
    private long id;
    private String title;
    private String description;
    private long creatorId;
    private boolean randomize;
    private boolean multiPage;
    private boolean immediateCorrect;
    private boolean practiceAllowed;
    private Timestamp createdAt;

    private List<Question> questions = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getCreatorId() { return creatorId; }
    public void setCreatorId(long creatorId) { this.creatorId = creatorId; }

    public boolean isRandomize() { return randomize; }
    public void setRandomize(boolean randomize) { this.randomize = randomize; }

    public boolean isMultiPage() { return multiPage; }
    public void setMultiPage(boolean multiPage) { this.multiPage = multiPage; }

    public boolean isImmediateCorrect() { return immediateCorrect; }
    public void setImmediateCorrect(boolean immediateCorrect) { this.immediateCorrect = immediateCorrect; }

    public boolean isPracticeAllowed() { return practiceAllowed; }
    public void setPracticeAllowed(boolean practiceAllowed) { this.practiceAllowed = practiceAllowed; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
