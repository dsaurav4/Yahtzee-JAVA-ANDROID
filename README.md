
# **Yahtzee Java-Android Project**

## **Overview**
This project is a digital implementation of the classic Yahtzee game, developed in Java for Android. It allows players to enjoy Yahtzee with a human-vs-computer mode, incorporating a detailed scoring system, helpful hints, and save/load functionality.

## **Features**
- **Dynamic Gameplay**:
  - Play against an AI opponent with strategic decision-making capabilities.
  - Highlights available scoring categories based on current dice rolls.
  - Animations for dice rolls to enhance user experience.
- **Save and Load Functionality**:
  - Save game progress and resume later.
  - Persistent game state storage.
- **AI Assistance**:
  - Offers hints for human players.
  - AI decisions based on optimized strategies.
- **Scoring System**:
  - Tracks points across categories with live updates.
  - Displays available points for each scoring category.
- **Logs**:
  - Tracks in-game events such as dice rolls, scoring decisions, and turn changes.

## **Technologies Used**
- **Programming Language**: Java
- **Framework**: Android SDK
- **Design Pattern**: Model-View-Controller (MVC)
- **Storage**: File-based serialization for saving and loading games.

## **Classes and Structure**
### **Model Classes**
- `Player` (Abstract): Represents a generic player with subclasses `Human` and `Computer`.
- `Dice`: Manages dice rolls.
- `ScoreCard`: Tracks scoring categories and calculates points.
- `Round`: Manages game rounds.
- `Logger`: Tracks and logs game events.

### **View Classes**
- `MainActivity`: Entry point to start or load a game.
- `DecideTurnActivity`: Determines the first player for a round.
- `RoundActivity`: Core gameplay UI for rolling dice, scoring, and switching turns.

### **AI Logic**
- Provides recommendations to human players.
- Implements strategic decision-making for computer players.

## **Development Timeline**
- **Model Development**: Player, Dice, and ScoreCard classes.
- **UI Integration**: Android Activities for gameplay and turn management.
- **Feature Additions**: Save/load functionality, AI logic, and hint system.
- **UI Enhancements**: Dynamic score highlights, dice roll animations, and custom resources.

## **Unique Features**
- Dynamic points preview for each category during gameplay.
- AI hints for strategic gameplay improvement.
- Comprehensive logging system for reviewing game history.

## **Setup Instructions**
1. Clone the repository:
   ```bash
   git clone https://github.com/dsaurav4/Yahtzee-JAVA-ANDROID.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and build the project.
4. Run the app on an Android emulator or a physical device.

## **Screenshots**

![Screenshot 1](screenshots/1.png)
![Screenshot 2](screenshots/2.png)
![Screenshot 3](screenshots/3.png)
![Screenshot 4](screenshots/4.png)
![Screenshot 5](screenshots/5.png)
![Screenshot 6](screenshots/6.png)
![Screenshot 7](screenshots/7.png)
![Screenshot 8](screenshots/8.png)
![Screenshot 9](screenshots/9.png)
![Screenshot 10](screenshots/10.png)
![Screenshot 11](screenshots/11.png)

---
