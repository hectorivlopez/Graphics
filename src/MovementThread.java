public class MovementThread extends Thread {
    public Window window;
    public MovementThread(Window window) {
        this.window = window;
    }

    public void run() {
        while (true) {
            if(window.bgPanel.away) {
                window.bgPanel.x++;
                window.bgPanel.y++;
            }
            else {
                window.bgPanel.x--;
                window.bgPanel.y--;
            }
            if(window.bgPanel.x == 100) {
                window.bgPanel.away = false;
            }
            if(window.bgPanel.x == 0) {
                window.bgPanel.away = true;
            }
            window.bgPanel.repaint();
            try {
                sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
