package com.nicktoony.cstopdown.rooms.serverlist;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.nicktoony.gameserver.service.client.Client;
import com.nicktoony.gameserver.service.client.models.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * A copy of {@link com.nicktoony.gameserver.service.libgdx.ServerList} that lives in the game
 * project (rather than the gameserver-service-libgdx submodule) so we can customise its
 * behaviour without modifying the submodule.
 *
 * Changes vs the submodule original:
 *  - Each server row is a single full-width clickable element, so clicking anywhere on the
 *    row (not just a specific column) joins the server.
 */
public class CSServerList extends Table implements Client.ClientListener {
    private final Skin skin;
    private final Client client;

    // Columns to display
    private List<String> columns = new ArrayList<String>();
    private List<String> metaColumns = new ArrayList<String>();

    // Stored values
    private ShapeRenderer shapeRenderer;
    private Table serversTable;
    private boolean updated;
    private boolean refreshing;
    private RowListener listener;

    // UI actors
    private Button buttonRefresh;
    private Label buttonRefreshText;
    private TextField textInputName;
    private Label labelName;
    private Label[] headerLabels;

    public interface RowListener {
        public void onSelected(Server server);
    }

    /**
     * Create a new ServerList, which is a libGDX table. It will handle the layout,
     * server list fetching. Just give it a skin.
     * @param skin
     */
    public CSServerList(Skin skin) {
        this.skin = skin;

        // Setup the table layout
        setFillParent(true);
        align(Align.topLeft);

        // Define columns
        columns.add("Name");
        columns.add("Players");

        // For debug purposes
        shapeRenderer = new ShapeRenderer();

        // start the initial refresh
        client = new Client().setListener(this);
        refresh();
    }

    /**
     * Call this to recreate all views
     */
    public void setup() {
        clearChildren();

        setupColumnHeaders();
        setupRows();
        setupButtons();
    }

    private void update() {
        updated = true;
    }

    /**
     * The top headers are a seperate layout to the actual serverlist table,
     * so that they don't scroll away
     */
    private void setupColumnHeaders() {
        headerLabels = createRow(this, columns.toArray(new String[columns.size()]), true);
    }

    /**
     * uses the client's servers to populate the rows
     */
    private void setupRows() {
        if (serversTable == null) {
            row();

            serversTable = new Table();
            serversTable.setDebug(getDebug());
            serversTable.top();

            ScrollPane scrollPane = new ScrollPane(serversTable);
            add(scrollPane).expandY().fill().colspan(getColumns());
        }

        for (Server server : client.getServers()) {
            // Collect together all the meta values and such
            List<String> values = new ArrayList<String>();
            values.add(server.getName());
            values.add(server.getCurrentPlayers() + "/" + server.getMaxPlayers());
            for (String key : metaColumns) {
                if (server.getMeta().containsKey(key)) {
                    values.add(server.getMeta().get(key));
                } else {
                    values.add("null");
                }
            }

            // Build the row as a single full-width table so the entire row is clickable,
            // not just the individual column labels.
            final Server serverFinal = server;
            Table rowTable = new Table();
            rowTable.setTouchable(Touchable.enabled);
            for (int i = 0; i < values.size(); i++) {
                Label label = new Label(values.get(i), skin);
                rowTable.add(label).align(Align.left).expandX().fillX();
            }
            rowTable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (listener != null) {
                        listener.onSelected(serverFinal);
                    }
                }
            });

            serversTable.row().expandX().fillX();
            serversTable.add(rowTable).expandX().fillX();
        }
    }

    /**
     * Any additional buttons and layouts are to be added here
     */
    private void setupButtons() {
        row().padTop(20);

        Table table = new Table();
        table.setDebug(getDebug());

        add(table).fillX().colspan(getColumns()).pad(5);

        buttonRefresh = new Button(skin);
        buttonRefreshText = new Label("Refresh", skin);
        buttonRefresh.add(buttonRefreshText);
        buttonRefresh.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!refreshing) {
                    refresh();
                }
            }
        });

        textInputName = new TextField("", skin);
        labelName = new Label("Server Name: ", skin);

        CheckBox checkEmpty = new CheckBox(" Empty Servers", skin);
        checkEmpty.setChecked(true);
        CheckBox checkFull = new CheckBox(" Full  Servers", skin);
        checkFull.setChecked(true);

        // States
        setRefreshing(refreshing);

        // Add to layouts
        table.row().left();
        table.add(labelName);
        table.add(textInputName).fillX().expandX();
        table.add(buttonRefresh).padLeft(50);

        table.row().left();
        table.add(checkEmpty);
        table.row().left();
        table.add(checkFull);
    }

    /**
     * To reduce redundant code, this method can be used to create a row
     * @param table
     * @param values
     * @param header
     */
    private Label[] createRow(Table table, String[] values, boolean header) {
        if (header) {
            table.row().padBottom(20);
        } else {
            table.row().expandX();
        }

        Label[] labels = new Label[values.length];
        for (int i = 0; i < values.length; i ++) {
            String value = values[i];
            Label label = new Label(value, skin);
            if (header) {
                table.add(label).expandX().align(Align.left);
            } else {
                table.add(label).align(Align.left).width(headerLabels[i].getWidth());
            }
            labels[i] = label;
        }
        return labels;
    }

    /**
     * Best to tidy up..
     */
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onRefreshed() {
        setRefreshing(false);
        update();
    }

    @Override
    public void onFail() {

    }

    /**
     * This updates the state of the button
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if (buttonRefresh != null) {
            if (refreshing) {
                buttonRefresh.setColor(Color.DARK_GRAY);
                buttonRefreshText.setText("Refreshing...");
            } else {
                buttonRefresh.setColor(Color.WHITE);
                buttonRefreshText.setText("Refresh");
            }
        }
    }

    /**
     *  Initiate a refresh
     */
    public void refresh() {
        client.refresh();
        setRefreshing(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (updated) {
            serversTable.clearChildren();
            setupRows();
            updated = false;
        }
    }

    public void addMetaColumn(String header, String column) {
        columns.add(header);
        metaColumns.add(column);
    }

    public void setListener(RowListener listener) {
        this.listener = listener;
    }
}
