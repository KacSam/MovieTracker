package org.example;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

public class MovieManagerGUI extends JFrame {
    private MovieDatabase movieDatabase;

    private DefaultListModel<String> unwatchedModel;
    private DefaultListModel<String> plannedModel;
    private DefaultListModel<String> watchedModel;

    private JList<String> unwatchedList;
    private JList<String> plannedList;
    private JList<String> watchedList;

    public MovieManagerGUI() {
        movieDatabase = MovieDatabase.getInstance(); 
        movieDatabase.loadFromJsonFile("movies.json");

        setTitle("Movie Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        setLayout(new BorderLayout());
        showMainMenu();
    }

    private void showMainMenu() {

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel menuLabel = new JLabel("MENU", JLabel.CENTER);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 34));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(menuLabel);

        Dimension buttonSize = new Dimension(250, 50);

        JButton searchMovieButton = new JButton("Wyszukaj film");
        searchMovieButton.setPreferredSize(buttonSize);
        searchMovieButton.setMaximumSize(buttonSize);
        searchMovieButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton manageMoviesButton = new JButton("Zarządzaj listą filmów");
        manageMoviesButton.setPreferredSize(buttonSize);
        manageMoviesButton.setMaximumSize(buttonSize);
        manageMoviesButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton statisticsMovieButton = new JButton("Pokaz Statystki");
        statisticsMovieButton.setPreferredSize(buttonSize);
        statisticsMovieButton.setMaximumSize(buttonSize);
        statisticsMovieButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitButton = new JButton("Wyjdź z aplikacji");
        exitButton.setPreferredSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(searchMovieButton);

        menuPanel.add(Box.createVerticalStrut(35));
        menuPanel.add(manageMoviesButton);

        menuPanel.add(Box.createVerticalStrut(35));
        menuPanel.add(statisticsMovieButton);

        menuPanel.add(Box.createVerticalStrut(35));
        menuPanel.add(exitButton);

        menuPanel.add(Box.createVerticalGlue());

        switchToPanel(menuPanel);

        searchMovieButton.addActionListener(e -> showSearchMoviePanel());
        manageMoviesButton.addActionListener(e -> showMovieListPanel());
        statisticsMovieButton.addActionListener(e -> showMovieStatisticsPanel());
        exitButton.addActionListener(e -> System.exit(0));

    }


    private void showSearchMoviePanel() {
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField movieTitleField = new JTextField(20);
        JButton searchButton = new JButton("Szukaj");
        JButton backButton = new JButton("Powrót");

        searchPanel.add(new JLabel("Tytuł filmu: "));
        searchPanel.add(movieTitleField);
        searchPanel.add(searchButton);
        searchPanel.add(backButton);

        switchToPanel(searchPanel);

        searchButton.addActionListener(e -> {
            String title = movieTitleField.getText().trim();
            if (!title.isEmpty()) {
                String jsonResponse = MovieSearch.searchMovie(title);
                Movie movie = JsonMovieParser.parseJsonFromApi(title);
                if (movie != null) {
                    boolean exists = false;
                    for (Movie existingMovie : movieDatabase.getMovies()) {
                        if (existingMovie.getImdbUrl().equals(movie.getImdbUrl())) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists) {
                        JOptionPane.showMessageDialog(this, "Ten film już jest na twojej liscie!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    } else {
                        int option = JOptionPane.showConfirmDialog(
                                this,
                                "Znaleziono film: " + movie + "\nCzy dodać do listy?",
                                "Wynik wyszukiwania",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (option == JOptionPane.YES_OPTION) {
                            movieDatabase.addMovie(movie);
                            movie.setState(new UnwatchedState());
                            JOptionPane.showMessageDialog(this, "Film dodany do listy!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Nie znaleziono filmu!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(e -> showMainMenu());
    }


    private void showMovieListPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 3));

        unwatchedModel = new DefaultListModel<>();
        plannedModel = new DefaultListModel<>();
        watchedModel = new DefaultListModel<>();

        unwatchedList = new JList<>(unwatchedModel);
        plannedList = new JList<>(plannedModel);
        watchedList = new JList<>(watchedModel);

        mainPanel.add(createListPanel("Nieoglądnięte", unwatchedList, true));  // Kolumna 1
        mainPanel.add(createListPanel("Planowane do oglądania", plannedList, true));  // Kolumna 2
        mainPanel.add(createListPanel("Obejrzane", watchedList, false));  // Kolumna 3

        refreshMovieLists();

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Powrót");
        wrapperPanel.add(backButton, BorderLayout.SOUTH);

        switchToPanel(wrapperPanel);

        backButton.addActionListener(e -> showMainMenu());
    }

    private JPanel createListPanel(String title, JList<String> list, boolean hasChangeStateButton) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);



        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedMovieTitle = list.getSelectedValue();
                    if (selectedMovieTitle != null) {
                        Movie selectedMovie = getMovieByTitle(selectedMovieTitle);
                        if (selectedMovie != null) {
                            JEditorPane editorPane = new JEditorPane();
                            editorPane.setContentType("text/html");
                            String htmlContent = "<html>" +
                                    "<div style='text-align: center;'>" +
                                    "<img src='" + selectedMovie.getPoster() + "' alt='Poster' width='150'><br>" +  // Dodanie plakatu
                                    "<b>Title:</b> " + selectedMovie.getTitle() + "<br>" +
                                    "<b>Runtime:</b> " + selectedMovie.getRuntime() + "<br>" +
                                    "<b>Genre:</b> " + selectedMovie.getGenre() + "<br>" +
                                    "<b>IMDb Rating:</b> " + selectedMovie.getImdbRating() + "<br>" +
                                    "<b>IMDb Link:</b> <a href='" + selectedMovie.getImdbUrl() + "'>" + selectedMovie.getImdbUrl() + "</a>" +
                                    "</div>" +
                                    "</html>";

                            editorPane.setText(htmlContent);
                            editorPane.setEditable(false);

                            editorPane.addHyperlinkListener(e -> {
                                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                                    String url = e.getURL().toString();
                                    try {
                                        // Otwieramy link w domyślnej przeglądarce
                                        Desktop.getDesktop().browse(new URI(url));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                            editorPane.setPreferredSize(new Dimension(300,350));
                            int choice = JOptionPane.showOptionDialog(
                                    panel,
                                    editorPane,
                                    "Szczegóły filmu: " + selectedMovie.getTitle(),
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE,
                                    null,
                                    new String[]{"Usuń film", "Zamknij"},
                                    "Zamknij"
                            );
                            if (choice == JOptionPane.YES_OPTION) {
                                int confirm = JOptionPane.showConfirmDialog(
                                        panel,
                                        "Czy na pewno chcesz usunąć film " + selectedMovie.getTitle() + "?",
                                        "Potwierdzenie usunięcia",
                                        JOptionPane.YES_NO_OPTION
                                );

                                if (confirm == JOptionPane.YES_OPTION) {
                                    movieDatabase.removeMovie(selectedMovie);
                                    refreshMovieLists();
                                    JOptionPane.showMessageDialog(panel, "Film został usunięty.", "Usunięto", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }

                        }
                    }
                    refreshMovieLists();
                }
            }
        });


        if (hasChangeStateButton) {
            JButton changeStateButton = new JButton("Przenieś");
            panel.add(changeStateButton, BorderLayout.SOUTH);

            changeStateButton.addActionListener(e -> {
                String selectedMovieTitle = list.getSelectedValue();
                if (selectedMovieTitle != null) {
                    Movie selectedMovie = getMovieByTitle(selectedMovieTitle);
                    if (selectedMovie != null) {
                        String[] options = {"Nieoglądnięte", "Planowane do oglądania", "Obejrzane"};
                        int choice = JOptionPane.showOptionDialog(
                                this,
                                "Zmień stan filmu: " + selectedMovie.getTitle(),
                                "Zmień stan",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                options[0]
                        );

                        switch (choice) {
                            case 0 -> selectedMovie.setState(new UnwatchedState());
                            case 1 -> selectedMovie.setState(new PlannedToWatchState());
                            case 2 -> {
                                String rating = JOptionPane.showInputDialog(
                                        this,
                                        "Podaj twoją ocenę filmu (1-10):",
                                        "Ocena filmu",
                                        JOptionPane.PLAIN_MESSAGE
                                );
                                if (rating != null && !rating.trim().isEmpty()) {
                                    try {
                                        int userRating = Integer.parseInt(rating.trim());
                                        if (userRating >= 1 && userRating <= 10) {
                                            selectedMovie.setUserRating(userRating);
                                            selectedMovie.setState(new WatchedState());
                                            JOptionPane.showMessageDialog(this, "Film oceniony na: " + userRating);
                                        } else {
                                            JOptionPane.showMessageDialog(this, "Ocena musi być liczbą od 1 do 10.", "Błąd", JOptionPane.ERROR_MESSAGE);
                                        }
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę.", "Błąd", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        }
                        refreshMovieLists();
                    }
                }
            });
        } else {
            JButton changeRatingButton = new JButton("Zmień ocenę");
            panel.add(changeRatingButton, BorderLayout.SOUTH);

            changeRatingButton.addActionListener(e -> {
                String selectedMovieTitle = list.getSelectedValue();
                if (selectedMovieTitle != null) {
                    Movie selectedMovie = getMovieByTitle(selectedMovieTitle);
                    if (selectedMovie != null && selectedMovie.getStateName().equals("Watched")) {
                        String rating = JOptionPane.showInputDialog(
                                this,
                                "Podaj nową ocenę filmu (1-10):",
                                "Zmień ocenę",
                                JOptionPane.PLAIN_MESSAGE
                        );
                        if (rating != null && !rating.trim().isEmpty()) {
                            try {
                                int userRating = Integer.parseInt(rating.trim());
                                if (userRating >= 1 && userRating <= 10) {
                                    selectedMovie.setUserRating(userRating);
                                    JOptionPane.showMessageDialog(this, "Ocena filmu została zmieniona na: " + userRating);
                                    String newTitle = selectedMovie.getTitle().substring(0, selectedMovie.getTitle().length() - 2) + userRating + ")";
                                    selectedMovie.setTitle(newTitle);
                                    refreshMovieLists(); // Odśwież listy
                                } else {
                                    JOptionPane.showMessageDialog(this, "Ocena musi być liczbą od 1 do 10.", "Błąd", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę.", "Błąd", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Ocena może być zmieniona tylko dla filmów obejrzanych.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });



        }

        return panel;
    }

    private void showMovieStatisticsPanel() {
        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Statystyki Filmów", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statisticsPanel.add(Box.createVerticalStrut(20)); // Odstęp od góry
        statisticsPanel.add(titleLabel);

        int totalWatchTime = MovieStatistics.sumWatchTime(movieDatabase);

        String favoriteGenres = MovieStatistics.getFavoriteGenre(movieDatabase);

        JLabel watchTimeLabel = new JLabel("Łączny czas oglądania: " + totalWatchTime + " minut");
        watchTimeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        watchTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statisticsPanel.add(Box.createVerticalStrut(20)); // Odstęp
        statisticsPanel.add(watchTimeLabel);

        JLabel favoriteGenresLabel = new JLabel("Ulubione gatunki: " + favoriteGenres);
        favoriteGenresLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        favoriteGenresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statisticsPanel.add(Box.createVerticalStrut(20)); // Odstęp
        statisticsPanel.add(favoriteGenresLabel);

        JButton backButton = new JButton("Powrót");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        statisticsPanel.add(Box.createVerticalStrut(30)); // Odstęp
        statisticsPanel.add(backButton);

        backButton.addActionListener(e -> showMainMenu());

        switchToPanel(statisticsPanel);
    }


    private void refreshMovieLists() {
        unwatchedModel.clear();
        plannedModel.clear();
        watchedModel.clear();

        for (Movie movie : movieDatabase.getMovies()) {
            String movieInfo = movie.getTitle();

            if (movie.getStateName().equals("Watched")) {
                if (!movie.getTitle().contains("Twoja ocena")) {
                    movieInfo += " (Twoja ocena: " + movie.getUserRating() + ")";
                    movie.setTitle(movieInfo);
                }
            }

            switch (movie.getStateName()) {
                case "Unwatched" -> unwatchedModel.addElement(movieInfo);
                case "Planned to Watch" -> plannedModel.addElement(movieInfo);
                case "Watched" -> watchedModel.addElement(movieInfo);
            }
        }
    }


    private Movie getMovieByTitle(String title) {
        for (Movie movie : movieDatabase.getMovies()) {
            if (movie.getTitle().equalsIgnoreCase(title)) {
                return movie;
            }
        }
        return null;
    }
    private void switchToPanel(JPanel panel) {
        getContentPane().removeAll();
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovieDatabase movieDatabase = MovieDatabase.getInstance();
            movieDatabase.loadFromJsonFile("movies.json");

            MovieManagerGUI gui = new MovieManagerGUI();
            gui.setVisible(true);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                movieDatabase.saveToJsonFile("movies.json");
                System.out.println("Dane zostały zapisane przed zamknięciem programu.");
            }));
        });
    }
}
