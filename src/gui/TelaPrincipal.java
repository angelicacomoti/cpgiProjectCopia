package gui;
import java.io.File;

import controladores.ControladorDeEventos;
import controladores.TipoDesenho;
import controladores.TipoFiltro;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import primitivos.Circulo;
import primitivos.LinhaPoligonal;
import primitivos.Poligono;
import primitivos.Ponto;
import primitivos.PontoGr;
import primitivos.Reta;
import primitivos.Retangulo;
import utils.AlertaCallback;
import utils.AlertaPersonalizado;
import utils.Figura;
import utils.XMLParser;

@SuppressWarnings("restriction")
public class TelaPrincipal {

	private Stage palco;
	private VBox menuAcoes;

	private Button filtroAlta;
	private Button filtroBaixa;
	private Button filtroCinza;

	private Button pontos;
	private Button retas;
	private Button circulos;

	private Button curvaDragao;
	private Button opcaoGeral;

    private Button retaElastica;
	private Button circuloElastico;
	private Button retanguloElastico;

	private Button poligonoElastico;
    private Button retaPoligonalElastica;

    private Button selecionarAreaClipping;
    private Button desfazerSelecaoClipping;
    private Button clipping;

    private Button abrirArquivo;
    private Button salvarArquivo;

    private Button limpar;
    private Button redesenhar;

	private Canvas canvas;
	private Canvas canvasLittle;
	private ControladorDeEventos controladorDeEventos;
	private FileChooser fileChooser;

	public static int LARGURA_MENU = 270;

	public static int LARGURA_CANVAS = 1100;
	public static int ALTURA_CANVAS = 800;

	public static int LARGURA_CANVAS_LITTLE = LARGURA_CANVAS / 5;
	public static int ALTURA_CANVAS_LITTLE = ALTURA_CANVAS / 5;

	public static int LARGURA_PALCO = LARGURA_CANVAS + LARGURA_MENU;
	public static int ALTURA_PALCO = ALTURA_CANVAS;


					

	public TelaPrincipal(Stage palco) {
		this.palco = palco;
		desenharTela();
	}

	public void desenharTela(){
			
		palco.setWidth(LARGURA_PALCO);
		palco.setHeight(ALTURA_PALCO);
		palco.setResizable(false);

		//criando Canvas
		canvas = new Canvas(LARGURA_CANVAS, ALTURA_CANVAS);

		//criando canvas pequeno
		BorderPane paneLittle = new BorderPane();
		paneLittle.setMaxWidth(LARGURA_CANVAS_LITTLE);
		paneLittle.setMinWidth(LARGURA_CANVAS_LITTLE);
		paneLittle.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		canvasLittle = new Canvas(LARGURA_CANVAS_LITTLE, ALTURA_CANVAS_LITTLE); // proporçao: divide o maior por 5
		paneLittle.setCenter(canvasLittle);


		controladorDeEventos = new ControladorDeEventos(canvas, canvasLittle);
		
		// Painel para os componentes
        BorderPane pane = new BorderPane();
        
        //Criando Menu
        menuAcoes = montarMenuOpcoesButton();
		menuAcoes.getChildren().addAll(paneLittle);
		menuAcoes.setMaxWidth(LARGURA_MENU);
		menuAcoes.setMinWidth(LARGURA_MENU);


    	// atributos do painel
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setCenter(canvas);
        pane.setRight(menuAcoes);
    	atribuirEventosAosComponentesGraficos();

        // cria e insere cena
        Scene scene = new Scene(pane);
        palco.setScene(scene);
        palco.show();
		
	}

    private VBox montarMenuOpcoesButton(){
        VBox menuTemp = new VBox();
		menuTemp.getChildren().addAll(new Label("Desenho Ponto a Ponto "), criarPrimeiraLinha());
        menuTemp.getChildren().addAll(new Label("Desenho Figuras"), criarSegundaLinha());
        menuTemp.getChildren().addAll(new Label("Desenho Figuras Elástica"), criarTerceiraLinha(), criarQuartaLinha());
        menuTemp.getChildren().addAll(new Label("Clipping "), criarQuintaLinha());
        menuTemp.getChildren().addAll(new Label("Arquivo "), criarArquivoLinha());
        menuTemp.getChildren().addAll(new Label("Opções "), criarOpcoesLinha()/*, criarUndoRedoLinha()*/);
		menuTemp.getChildren().addAll(new Label("Filtros "), criarLinhaFiltros());
		menuTemp.getChildren().addAll(new Label(""), criarOpcaoCor(), criarOpcaoEspessura());
		menuTemp.setSpacing(10);

		menuTemp.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		menuTemp.setPadding(new Insets(10, 10, 15, 10));
        return menuTemp;
    }

    private HBox criarPrimeiraLinha(){
		HBox menuDesenhoPontoPonto = new HBox();
        menuDesenhoPontoPonto.setSpacing(10);
        pontos = criarButton("Pontos");
        retas = criarButton("Retas");
        circulos = criarButton("Circulos");
        menuDesenhoPontoPonto.getChildren().addAll(pontos, retas, circulos);
        return menuDesenhoPontoPonto;
    }
	private HBox criarSegundaLinha(){
		HBox desenhoFigura = new HBox();
		desenhoFigura.setSpacing(10);
		curvaDragao = criarButton("Cv. Dragão");
		opcaoGeral = criarButton("Opção Geral");
		desenhoFigura.getChildren().addAll(curvaDragao, opcaoGeral);
		return desenhoFigura;
	}
	private HBox criarTerceiraLinha(){
		HBox menuDesenhoElastico = new HBox();
		menuDesenhoElastico.setSpacing(10);
		retaElastica = criarButton("Retas");
		circuloElastico = criarButton("Circulos");
		retanguloElastico = criarButton("Retângulo");
		menuDesenhoElastico.getChildren().addAll(retaElastica, circuloElastico, retanguloElastico);
		return menuDesenhoElastico;
	}
	private HBox criarQuartaLinha(){
		HBox menuDesenhoElastico2 = new HBox();
		menuDesenhoElastico2.setSpacing(10);
		poligonoElastico = criarButton("Poligono");
		retaPoligonalElastica = criarButton("Reta Poligonal");
		menuDesenhoElastico2.getChildren().addAll(poligonoElastico, retaPoligonalElastica);
		return menuDesenhoElastico2;
	}
	private HBox criarQuintaLinha(){
		HBox menuClipping = new HBox();
		menuClipping.setSpacing(10);
		selecionarAreaClipping = criarButton("Selecionar");
		desfazerSelecaoClipping = criarButton("Deselecionar");
		clipping = criarButton("Recortar");
		menuClipping.getChildren().addAll(selecionarAreaClipping, desfazerSelecaoClipping, clipping);
		return menuClipping;
	}
	private HBox criarSextaLinha(){
		HBox menuClipping2 = new HBox();
		menuClipping2.setSpacing(10);
		clipping = criarButton("Recortar");
		menuClipping2.getChildren().addAll(clipping);
		return menuClipping2;
	}
	private HBox criarArquivoLinha(){
		HBox menuArquivo = new HBox();
		menuArquivo.setSpacing(10);
		abrirArquivo = criarButton("Abrir");
		salvarArquivo = criarButton("Salvar");
		menuArquivo.getChildren().addAll(abrirArquivo, salvarArquivo);
		return menuArquivo;
	}
	private HBox criarOpcoesLinha(){
		HBox menuOpcoes = new HBox();
		menuOpcoes.setSpacing(10);
		limpar = criarButton("Limpar");
		redesenhar = criarButton("Redesenhar");
		menuOpcoes.getChildren().addAll(limpar, redesenhar);
		return menuOpcoes;
	}

	private HBox criarLinhaFiltros(){
		HBox menuFiltros = new HBox();
		menuFiltros.setSpacing(10);
		filtroAlta = criarButton("Alta");
		filtroBaixa = criarButton("Baixa");
		filtroCinza = criarButton("Cinzas");
		menuFiltros.getChildren().addAll(filtroAlta, filtroBaixa, filtroCinza);
		return menuFiltros;
	}

    private Button criarButton(String text){
        Button btn = new Button(text);
        btn.setMinHeight(20);
        btn.setMinWidth(43);
        btn.setFont(new Font(12));
        return btn;
    }

	@SuppressWarnings("restriction")
	private HBox criarOpcaoCor(){
		HBox hbox = new HBox();
		ColorPicker colorPicker = new ColorPicker(Color.BLACK);
		colorPicker.setOnAction(e -> {
			controladorDeEventos.getDesenhador().setCor(colorPicker.getValue());
		});
		hbox.getChildren().addAll(new Label("Cor: "), colorPicker);
		return hbox;
	}

	@SuppressWarnings("restriction")
	private HBox criarOpcaoEspessura(){
		HBox hbox = new HBox();
		Spinner<Integer> diametroLinhas = new Spinner<Integer>();
		SpinnerValueFactory<Integer> diametros = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2);
		diametroLinhas.setValueFactory(diametros);
		diametroLinhas.setMaxWidth(80);
		diametroLinhas.valueProperty().addListener(e -> {
			controladorDeEventos.getDesenhador().setDiametro(diametros.getValue());
		});
		hbox.getChildren().addAll(new Label("Espessura: "), diametroLinhas);
		return hbox;
	}

	private void atribuirEventosAosComponentesGraficos() {
		// menu
        this.pontos.setOnAction(e -> {
            controladorDeEventos.getEventoBasicoMenuDesenho(TipoDesenho.PONTO);
        });
        this.retas.setOnAction(e -> {
            controladorDeEventos.getEventoBasicoMenuDesenho(TipoDesenho.RETA);
        });
        this.circulos.setOnAction(e -> {
            controladorDeEventos.getEventoBasicoMenuDesenho(TipoDesenho.CIRCULO);
        });
		this.curvaDragao.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.CURVA_DO_DRAGAO);
		});
		this.opcaoGeral.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.OPCAO_GERAL);
		});

		this.limpar.setOnAction(e -> {
			AlertaPersonalizado.criarAlertaComCallback("A execucao dessa operacao resulta na perda de todos os dados desenhados.\n "
					+ "Deseja continuar?", new AlertaCallback() {
				@Override
				public void alertaCallbak() {
					controladorDeEventos.limparCanvas();
				}
			});
		});

		this.redesenhar.setOnAction(e -> {
			controladorDeEventos.redesenhar();
		});
        this.retaElastica.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.RETA_ELASTICA);
		});
		this.circuloElastico.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.CIRCULO_ELASTICO);
		});
		this.retanguloElastico.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.RETANGULO_ELASTICO);
		});
		this.poligonoElastico.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.POLIGONO_ELASTICO);
		});
		this.retaPoligonalElastica.setOnAction(e -> {
			controladorDeEventos.setTipoDesenho(TipoDesenho.RETA_POLIGONAL);
		});

		this.selecionarAreaClipping.setOnAction(ev -> {
			this.controladorDeEventos.setTipoDesenho(TipoDesenho.SELECIONAR_AREA_CLIPPING);
		});
		
		this.desfazerSelecaoClipping.setOnAction(ev -> {
			this.controladorDeEventos.desfazerSelecaoClipping();
		});
		
		this.clipping.setOnAction(ev -> {
			this.controladorDeEventos.recortar();
		});
		
		this.abrirArquivo.setOnAction(ev -> {
			AlertaPersonalizado.criarAlertaComCallback("A execução dessa operacão resulta na perda de todos os desenhos não salvos.\n "
					+ "Deseja continuar?", new AlertaCallback() {				
						@Override
						public void alertaCallbak() {
							abriXML();
						}
					});
		});
		
		this.salvarArquivo.setOnAction(ev -> {
			fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
			File file = fileChooser.showSaveDialog(this.palco);
			if (file != null) {
				try {
					XMLParser<Figura> parser = new XMLParser<Figura>(file);
					Figura figura = new Figura();
					figura.setObjetosDesenhados(this.controladorDeEventos.getDesenhador().getObjetosDesenhados());
					parser.saveFile(figura, new Class[] {
							Figura.class,
							Retangulo.class,
							Ponto.class,
							Reta.class,
							Circulo.class,
							Poligono.class,
							PontoGr.class
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		filtroAlta.setOnAction(event -> {
			controladorDeEventos.onFiltro(TipoFiltro.ALTA);
		});

		filtroBaixa.setOnAction(event -> {
			controladorDeEventos.onFiltro(TipoFiltro.BAIXA);
		});

		filtroCinza.setOnAction(event -> {
			controladorDeEventos.onFiltro(TipoFiltro.CINZA);
		});


		// canvas
		canvas.setOnMouseMoved(event -> {
			palco.setTitle("(Posição do Cursor):" + " (" + (int) event.getX() + ", " + (int) event.getY() + ")");
		});
		canvas.setOnMousePressed(event -> {
			controladorDeEventos.onCanvasMousePressed(event);
		});

		canvas.setOnMouseDragged(ev -> {
			controladorDeEventos.onMouseDraggedPrimitivosElasticos(ev);
		});

		canvas.setOnMouseReleased(ev -> {
			controladorDeEventos.onMouseReleasedPrimitivosElasticos(ev);
		});

	}
	
	private void abriXML() {
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		File file = fileChooser.showOpenDialog(this.palco);
		if (file != null) {
			try {
				XMLParser<Figura> parser = new XMLParser<Figura>(file);
				Figura figura = parser.toObject(new Class[] {
						Figura.class,
						Retangulo.class,
						Ponto.class,
						Reta.class,
						Ponto.class,
						Circulo.class,
						Poligono.class,
						LinhaPoligonal.class,
						PontoGr.class
				});
				this.controladorDeEventos.getDesenhador().setObjetosDesenhados(figura.getObjetosDesenhados());
				this.controladorDeEventos.getDesenhador().desenharObjetosArmazenados(null);
				this.controladorDeEventos.salvarCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}