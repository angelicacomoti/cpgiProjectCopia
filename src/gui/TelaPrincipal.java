package gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import primitivos.Ponto;

public class TelaPrincipal{

    Ponto pontoSelecionado = null;
    private Stage palco;
    private MenuBar menu;
    private Menu desenhoPontoPonto;
    private Menu desenhoElastico;
    private Menu opcoes;
    private MenuItem menuLimpar;
    private MenuItem menuPontos;
    private MenuItem menuRetas;
    private MenuItem menuCirculos;
    private MenuItem menuCurvaDragao;
    private MenuItem menuRetaElastica;
    private Canvas canvasLayer1;
    private Canvas canvasLayer2;
    private ControladorDeEventos controladorDeEventos;
	
	public TelaPrincipal(Stage palco) {
		this.palco = palco;
		desenharTela();
	}

	public void desenharTela(){
			
		palco.setWidth(750);
		palco.setHeight(750);

		//criando Canvas
        canvasLayer1 = new Canvas(palco.getWidth(), palco.getHeight());
		controladorDeEventos = new ControladorDeEventos(canvasLayer1);  
		canvasLayer2 = new Canvas(canvasLayer1.getWidth(),canvasLayer1.getHeight());
		
		// Painel para os componentes
        BorderPane pane = new BorderPane();
        
        //Criando Menu
        menu = new MenuBar();
        desenhoPontoPonto = new Menu("Desenho Ponto a Ponto");
        desenhoElastico = new Menu("Desenho com El�sticos");
        opcoes = new Menu("Op��es");
        menuPontos = new MenuItem("Pontos");
        menuRetas = new MenuItem("Retas");
        menuCirculos = new MenuItem("C�rculos");
        menuLimpar = new MenuItem("Limpar");
    	menuCurvaDragao = new MenuItem("Curva do Drag�o");
    	menuRetaElastica = new MenuItem("Retas");
    	desenhoPontoPonto.getItems().addAll(menuPontos,menuRetas,menuCirculos, menuCurvaDragao);
    	desenhoElastico.getItems().addAll(menuRetaElastica);
    	opcoes.getItems().add(menuLimpar);
    	menu.getMenus().addAll(desenhoPontoPonto,desenhoElastico,opcoes);
    	
    	//Criando footer
    	GridPane grid = montarMenuOpcoesGraficas();
    	VBox menus = new VBox();
    	menus.getChildren().addAll(menu,grid);
    	        
    	// atributos do painel
        pane.setBackground(new Background(new BackgroundFill(Color.AZURE, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setCenter(canvasLayer1); // posiciona o componente de desenho
        pane.setTop(menus);
        
    	atribuirEventosAosComponentesGraficos();
        // cria e insere cena
        Scene scene = new Scene(pane);
        palco.setScene(scene);
        palco.show();
		
	}
	
	private void atribuirEventosAosComponentesGraficos(){
		//menu
		this.menuRetas.setOnAction(e->{
			controladorDeEventos.getEventoBasicoMenuDesenho(TipoDesenho.RETA);
		});
		
		this.menuPontos.setOnAction(e->{
			controladorDeEventos.getEventoBasicoMenuDesenho(TipoDesenho.PONTO);
		});
		this.menuCirculos.setOnAction(e->{
			controladorDeEventos.getEventoBasicoMenuDesenho(TipoDesenho.CIRCULO);
		});
		this.menuLimpar.setOnAction(e->{
			controladorDeEventos.limparCanvas();
		});
		this.menuCurvaDragao.setOnAction(e->{
			controladorDeEventos.setTipoDesenho(TipoDesenho.CURVA_DO_DRAGAO);
		});
		this.menuRetaElastica.setOnAction(e->{
			controladorDeEventos.setTipoDesenho(TipoDesenho.RETA_ELASTICA);
		});
		
		//canvas
        canvasLayer1.setOnMouseMoved(event -> {
            palco.setTitle("(Posi��o do Cursor):" + " (" + (int) event.getX() + ", " + (int) event.getY() + ")");
        });
        canvasLayer1.setOnMousePressed(event -> {
        	controladorDeEventos.onCanvasMousePressed(event);
        });	
        
        canvasLayer1.setOnMouseDragged(ev->{
        	controladorDeEventos.onMouseDraggedPrimitivosElasticos(ev);
        });
        
        canvasLayer1.setOnMouseReleased(ev->{
        	controladorDeEventos.onMouseReleasedPrimitivosElasticos(ev);
        });

        
	}
	
	private GridPane montarMenuOpcoesGraficas() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(5);
		
		// Color Picker 
		ColorPicker colorPicker = new ColorPicker(Color.BLACK);
		colorPicker.setOnAction(e->{
			controladorDeEventos.setCor(colorPicker.getValue());
		});
		
		Spinner<Integer> diametroLinhas = new Spinner<Integer>();
		SpinnerValueFactory<Integer> diametros = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,20,3);
		diametroLinhas.setValueFactory(diametros);
		diametroLinhas.valueProperty().addListener(e->{
			controladorDeEventos.setDiametro(diametros.getValue());
		});
		
		grid.add(new Label("Cor: "), 0, 0);
		grid.add(colorPicker, 1, 0);
		grid.add(new Label("Di�metro dos Pontos: "),2, 0);
		grid.add(diametroLinhas, 3, 0);
				
		return grid;
	}
	
}
