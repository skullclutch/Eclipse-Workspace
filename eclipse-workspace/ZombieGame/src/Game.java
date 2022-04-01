import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
	public static int WIDTH = 800, HEIGHT = 608;
    public String title = "Zombie Game";

    private Thread thread;
    private boolean isRunning = false;
    
    //Instances
    private Handler handler;
    private KeyInput input;
    private MouseInput minput;
    private Camera cam;

    public Game(){
        //Constructer
        new Window(WIDTH, HEIGHT, title, this);
        start();
        
        
        init();
        //Below Here
   	 

         
    }
    
    private void init() {
    	 handler = new Handler();
    	 input = new KeyInput();
    	 cam= new Camera(0,0,handler);
    	 minput = new MouseInput(handler, cam);
    	 this.addKeyListener(input);
    	 this.addMouseListener(minput);
    	 
    	 
    	 handler.addObject(new Player(100, 100, ID.Player, input));
    	 
    	 handler.addObject(new Box(100,100,ID.Block));
    	 handler.addObject(new Box(200,200,ID.Block));
    	 handler.addObject(new Box(300,300,ID.Block));
    	 handler.addObject(new Box(400,400,ID.Block));
    	 
    	 minput.findPlayer();
    }

    private synchronized void start(){
        if(isRunning) return;

        thread = new Thread(this);
        thread.start();
        
        isRunning = true;
    }

    private synchronized void stop(){
        if(!isRunning) return;

        try{
            thread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    //gameloop
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountofTicks = 60.0;
        double ns = 1000000000 / amountofTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }

    private void tick(){
        //Updates the game
    	handler.tick();
    	cam.tick();
    }

    private void render(){
        //Renders the game
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //Meat and bones of our rendering
        g.setColor(Color.gray);
        g.fillRect(0,0,WIDTH,HEIGHT);
        
        g2d.translate(-cam.getX(), -cam.getY());
        
        handler.render(g);
        
        g2d.translate(cam.getX(), cam.getY());


        bs.show();
        g.dispose();
    }

    public static void main(String[] args) {
        new Game();
    }
}
