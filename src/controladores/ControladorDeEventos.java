package controladores;

import java.util.List;

import calculadores.CurvaDoDragaoCalculador;
import calculadores.RetaCalculador;
import gui.Desenhador;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import primitivos.LinhaPoligonal;
import primitivos.Poligono;
import primitivos.Ponto;
import primitivos.Reta;

public class ControladorDeEventos {

	private int iteracoesCurvaDragao;
	private Canvas canvas;
	private Canvas canvasLittle;
	private TipoDesenho tipoDesenho;
	private Ponto pontoAtual;
	private WritableImage backup;
	private WritableImage backupLittle;
	private boolean fimElastico;
	private Desenhador desenhador;

	public ControladorDeEventos(Canvas canvas, Canvas canvasLittle) {
		super();
		this.canvas = canvas;
		this.canvasLittle = canvasLittle;
		this.iteracoesCurvaDragao = 0;
		fimElastico = true;
		this.desenhador = new Desenhador(this.canvas, this.canvasLittle);
	}
	
	public Desenhador getDesenhador() {
		return desenhador;
	}

	public void setTipoDesenho(TipoDesenho tipoDesenho) {
		this.tipoDesenho = tipoDesenho;
		resetCanvas();
	}
	
	public void onCanvasMousePressed(MouseEvent event) {
		Ponto pontoClicado = new Ponto(event.getX(), event.getY());
		if (tipoDesenho != null){
			onCanvasMousePressedDesenho(event, pontoClicado);
		}
	}
	
	private void onMousePressedPrimitivosBasicos(Ponto pt) {

		if (pontoAtual == null) {
			pontoAtual = pt;
		} else {
			switch (tipoDesenho) {
			case RETA:
				this.desenhador.desenharReta(pontoAtual,pt,fimElastico);
				break;
			case CIRCULO:
				this.desenhador.desenharCirculo(pontoAtual,pt,fimElastico);
				break;
			default:
				throw new RuntimeException("Erro interno");
			}
			pontoAtual = null;
		}
	}

	private void onMousePressedPrimitivosElasticos(Ponto pt) {
		if (pontoAtual == null) {
			pontoAtual = pt;
			salvarCanvas();
			fimElastico = false;
		}
	}
	
	private void onMousePressedPoligonosElasticos(Ponto pt){
		if (pontoAtual == null) {
			Poligono poligono = (tipoDesenho.equals(TipoDesenho.POLIGONO_ELASTICO)) 
					? new Poligono(desenhador.getCor()) 
					: new LinhaPoligonal(desenhador.getCor());
			this.desenhador.setPoligonoEmDesenho(poligono);
			pontoAtual = pt;
			fimElastico = false;
		}
		salvarCanvas();
	}
		
	public void onMouseDraggedPrimitivosElasticos(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			if (!fimElastico) {
				// Seta canvas para estado capturado quando o mouse foi pressionado
				canvas.getGraphicsContext2D().drawImage(backup, 0, 0);
				canvasLittle.getGraphicsContext2D().drawImage(backupLittle, 0, 0);
				// Desenha sobre o "estado" capturado quando mouse foi pressionado		
				Ponto ptFinal = new Ponto(event.getX(), event.getY());
				this.desenhador.desenharPrimitivoElastico(pontoAtual,ptFinal, tipoDesenho, fimElastico);
				fimElastico = false;
			}
		}
	}

	public void onMouseReleasedPrimitivosElasticos(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			if (!fimElastico) {
				Ponto ptFinal = new Ponto(event.getX(), event.getY());
				//Atualiza se n�o estiver desenhando poligono elastico 
				fimElastico =  !isPoligonoElastico();
				this.desenhador.desenharPrimitivoElastico(pontoAtual,ptFinal,tipoDesenho, (fimElastico || isPoligonoElastico()) );
				//Se estiver desenhando poligono elastico, precisa usar o ultimo ponto para desenhar a proxima reta
				pontoAtual = (isPoligonoElastico()) ? ptFinal : null;
			}
		}
	}
	
	private void desenharCurvaDoDragao() {
		if (iteracoesCurvaDragao <= 17) {
			preencherCanvasCurvaDoDragao();
			this.iteracoesCurvaDragao += 1;
		} else {
			Alert alerta = new Alert(AlertType.WARNING, "A aplica��o atingiu o m�ximo de itera��es poss�veis.",
					ButtonType.FINISH);
			alerta.show();
		}
	}
	
	private void preencherCanvasCurvaDoDragao() {
		
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); //
		canvasLittle.getGraphicsContext2D().clearRect(0, 0, canvasLittle.getWidth(), canvasLittle.getHeight()); //

		Reta reta = new Reta(new Ponto(150, 400), new Ponto(600, 400), this.desenhador.getCor());
		CurvaDoDragaoCalculador calc = new CurvaDoDragaoCalculador(reta, this.iteracoesCurvaDragao);
		List<Reta> retasCurvaDragao;

		try {
			retasCurvaDragao = calc.getRetasCurva();
			for (Reta retaCalc : retasCurvaDragao) {
				this.desenhador.desenharPontos(RetaCalculador.obterPontos(retaCalc), this.desenhador.getCor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onCanvasMousePressedDesenho(MouseEvent event, Ponto pontoClicado){
		if (event.getButton() == MouseButton.PRIMARY ) {			
			//Definir qual desenho ser� feito
			switch (tipoDesenho) {
				case CURVA_DO_DRAGAO:
					desenharCurvaDoDragao();
					break;
				case PONTO:
					this.desenhador.desenharPonto((int) Math.floor(event.getX()), (int) Math.floor(event.getY()));
					break;
				case RETA:
				case CIRCULO:
					onMousePressedPrimitivosBasicos(pontoClicado);
					break;
				case RETA_ELASTICA:
				case CIRCULO_ELASTICO:
				case RETANGULO_ELASTICO:
				case SELECIONAR_AREA_CLIPPING:
					onMousePressedPrimitivosElasticos(pontoClicado);
				case POLIGONO_ELASTICO:
				case RETA_POLIGONAL:
					onMousePressedPoligonosElasticos(pontoClicado);
					break;
			}
		}else if(event.getButton() == MouseButton.SECONDARY && this.desenhador.isPoligonoElasticoEmDesenho()){
			// Captura clique com o bot�o secund�rio do mouse quando usuario est� desenhando poligonos
			switch (tipoDesenho) {
				case POLIGONO_ELASTICO:
					Ponto ptInicio = this.desenhador.getPoligonoEmDesenho().getRetas().get(0).getA();
					this.desenhador.desenharPoligono(pontoAtual, ptInicio, true);
					this.desenhador.salvarPoligonoDesenhado(TipoPrimitivo.POLIGONO);
					break;
				case RETA_POLIGONAL:
					this.desenhador.salvarPoligonoDesenhado(TipoPrimitivo.LINHA_POLIGONAL);
					break;
			}
			resetCanvas();
			resetPoligonoEmDesenho();
		}
	}
		
	public void getEventoBasicoMenuDesenho(TipoDesenho desenho) {
		tipoDesenho = desenho;
		resetCanvas();
	}
	
	public void limparCanvas() {
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvasLittle.getGraphicsContext2D().clearRect(0, 0, canvasLittle.getWidth(), canvasLittle.getHeight());
		this.desenhador.inicilizarEstruturasManipulacaoDeDesenhos();
		resetCanvas();
		resetPoligonoEmDesenho();
	}

	private void salvarCanvas(){
		// Capturando estado do canvas para desenhar sobre ele
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.WHITE);
		backup = canvas.snapshot(params, backup);
		backupLittle = canvasLittle.snapshot(params, backupLittle);
	}
	
	public void recortar(){
		if (this.getDesenhador().getObjetosDesenhados().size() > 0) {
			canvas.getGraphicsContext2D().drawImage(backup,0,0);
			canvasLittle.getGraphicsContext2D().drawImage(backupLittle,0,0);

			SnapshotParameters params = new SnapshotParameters();
			params.setViewport(this.desenhador.getAreaRecorte());
			WritableImage imagemRecortada = canvasLittle.snapshot(params,null);

			canvasLittle.getGraphicsContext2D().clearRect(0, 0, imagemRecortada.getWidth(), imagemRecortada.getHeight());
			canvasLittle.getGraphicsContext2D().drawImage(imagemRecortada,0,0);
		}
	}
	
	private void resetCanvas(){
		fimElastico = true;
		pontoAtual = null;
		iteracoesCurvaDragao = 0;
	}
	
	private void resetPoligonoEmDesenho() {
		this.desenhador.setPoligonoEmDesenho(null);
	}
		
	private Boolean isPoligonoElastico(){
		return tipoDesenho.equals(TipoDesenho.POLIGONO_ELASTICO) || (tipoDesenho.equals(TipoDesenho.RETA_POLIGONAL));
	}
	
	public void desfazerSelecaoClipping() {
		if(this.desenhador.getPoligonoEmDesenho() != null) {
			this.desenhador.salvarPoligonoDesenhado(TipoPrimitivo.LINHA_POLIGONAL);
		} 
		this.desenhador.desenharObjetosArmazenados(null);
	}
	
}
