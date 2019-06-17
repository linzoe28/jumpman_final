package view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import objects.Coin;
import objects.Fireball;
import objects.Jumpman;
import world.World;

public class View {

    /* SIZE */
    public static final ReadOnlyDoubleProperty WIDTH = new ReadOnlyDoubleWrapper(480).getReadOnlyProperty();
    public static final ReadOnlyDoubleProperty HEIGHT = new ReadOnlyDoubleWrapper(240).getReadOnlyProperty();

    /* LIMITS FOR SCROLLING */
    public static final ReadOnlyDoubleProperty LIMIT_LOW = new ReadOnlyDoubleWrapper(80).getReadOnlyProperty();
    public static final ReadOnlyDoubleProperty LIMIT_HIGH = new ReadOnlyDoubleWrapper(240).getReadOnlyProperty();

    /* POSITION IN WORLD (BOTTOM LEFT CORNER) */
    private final DoubleProperty x = new SimpleDoubleProperty(-80);
    private final DoubleProperty y = new SimpleDoubleProperty(-32);

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void scrollWindow() {
        Jumpman jm = World.getInstance().getJumpman();

        if (jm.getPosition().getX() - x.get() > LIMIT_HIGH.get()) {
            x.set(x.get() + (jm.getPosition().getX() - x.get() - LIMIT_HIGH.get()));
        } else if (jm.getPosition().getX() - x.get() < LIMIT_LOW.get()) {
            x.set(x.get() - (LIMIT_LOW.get() - (jm.getPosition().getX() - x.get())));
        }
    }

    /* NODES */
    private AnchorPane root;
    private Group view;
    private Help help;

    public AnchorPane getRoot() {
        return root;
    }

    public void initializeView() {
        root = new AnchorPane();
        view = new Group();

        Scale viewScale = Transform.scale(1, 1, 0, 0);  //縮放特效(可以縮放任何一種物件)
        viewScale.xProperty().bind(root.widthProperty().divide(View.WIDTH)); //divide是除法函式，以圖片根外框的大小比例，調整圖片縮放情況。
        viewScale.yProperty().bind(root.heightProperty().divide(View.HEIGHT));
        view.getTransforms().add(viewScale);

        view.setFocusTraversable(true);  //setFocusTraversable 讓該視窗取得輸入的控制權
        view.addEventHandler(KeyEvent.ANY, new Controller());  //聽鍵盤事件

        root.getChildren().add(view);

        Text info = TextBuilder.create()  //設定文字
                .text("Esc: Help\nR: Reset")
                .font(Font.font("Arial", FontWeight.BOLD, 16))
                .fill(Color.WHITE)
                .build();
        AnchorPane.setTopAnchor(info, 20.0);  //設定info擺放位置
        AnchorPane.setRightAnchor(info, 20.0);

        root.getChildren().add(info);

        Text time = TextBuilder.create()
                .font(Font.font("Arial", FontWeight.BOLD, 16))
                .fill(Color.WHITE)
                .build();
        time.textProperty().bind(Bindings.concat("Time: ", Bindings.format("%.2f", World.getInstance().levelTimeProperty()), " seconds"));

        AnchorPane.setTopAnchor(time, 20.0);
        AnchorPane.setLeftAnchor(time, 20.0);
        
        Text score = TextBuilder.create()
                .font(Font.font("Arial", FontWeight.BOLD, 16))
                .fill(Color.WHITE)
                .build();
        score.textProperty().bind(Bindings.concat("Score: ", Bindings.format("%d", World.getInstance().getLevelScore()), ""));

        AnchorPane.setTopAnchor(score, 40.0);
        AnchorPane.setLeftAnchor(score, 20.0);

        root.getChildren().addAll(time, score);

        help = new Help();
        root.getChildren().add(help);
    }

    public void clearView() {
        view.getChildren().clear();
    }

    public void loadView() {
        World w = World.getInstance();

        view.getChildren().add(w.getBackground().getNode());
        view.getChildren().add(w.getGoal().getNode());
        view.getChildren().add(w.getJumpman().getNode());

        for (Fireball f : w.getFireballs()) {
            view.getChildren().add(f.getNode());
        }

        for (Coin coin : w.getCoins()) {
            view.getChildren().add(coin.getNode());
        }

        w.getFireballs().addListener(new ListChangeListener<Fireball>() {
            @Override
            public void onChanged(Change<? extends Fireball> change) {
                while (change.next()) {
                    for (Fireball f : change.getAddedSubList()) {
                        view.getChildren().add(f.getNode());
                    }
                    for (Fireball f : change.getRemoved()) {
                        view.getChildren().remove(f.getNode());
                    }
                }
            }
        });

        w.getCoins().addListener(new ListChangeListener<Coin>() {
            @Override
            public void onChanged(Change<? extends Coin> c) {
                while (c.next()) {
                    for (Coin coin : c.getRemoved()) {
                        view.getChildren().remove(coin.getNode());
                    }
                }
            }
        });

        x.set(-80);
        y.set(-32);
    }

    public Help getHelp() {
        return help;
    }

    /* SINGLETON */
    private static final View instance = new View();

    private View() {
    }

    public static View getInstance() {
        return instance;
    }
}
