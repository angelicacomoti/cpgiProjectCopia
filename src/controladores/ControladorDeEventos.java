package controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import calculadores.*;
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
import primitivos.*;

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
				canvas.getGraphicsContext2D().drawImage(backup, 0, 0);
				canvasLittle.getGraphicsContext2D().drawImage(backupLittle, 0, 0);
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
				fimElastico =  !isPoligonoElastico();
				this.desenhador.desenharPrimitivoElastico(pontoAtual,ptFinal,tipoDesenho, (fimElastico || isPoligonoElastico()) );
				pontoAtual = (isPoligonoElastico()) ? ptFinal : null;
			}
		}
	}

	private void desenharOpcaoGeral(Ponto pontoMedio) {
		int raio = 100;

		List<Circulo> circulosCircunferencia = new ArrayList<>();
		Circulo circuloCentral = new Circulo(raio, pontoMedio, Color.GREEN);
		circulosCircunferencia.add(circuloCentral);

		List<Ponto> pontos = determinarPontos(pontoMedio, raio);
		desenharPontosDesenhoGeral(pontos);

		circulosCircunferencia.addAll(determinarCirculos(pontos, raio));
		desenharCirculosDesenhoGeral(circulosCircunferencia);

		List<Reta> retas = determinarRetasCirculoCentral(pontoMedio, pontos);
		desenharRetasDesenhoGeral(retas);

		List<Ponto> pontosExtremos = determinarPontosExtremos(pontoMedio, raio);
		desenharPontosDesenhoGeral(pontosExtremos);

		List<Reta> retasExtremas = determinarRetasExtremas(pontoMedio, pontosExtremos);
		desenharRetasDesenhoGeral(retasExtremas);

		List<Reta> retasExtremas2 = determinarRetasExtremas2(pontosExtremos);
		desenharRetasDesenhoGeral(retasExtremas2);

		this.desenhador.setCor(Color.BLACK);
	}

	private List<Ponto> determinarPontos(Ponto pontoMedio, int raio){
		List<Ponto> pontos = new ArrayList<>();
		pontos.add(new Ponto(pontoMedio.getx() + 100, pontoMedio.gety())); //leste
		pontos.add(new Ponto(pontoMedio.getx() + 50, pontoMedio.gety()-raio+12)); //nordeste
		pontos.add(new Ponto(pontoMedio.getx() - 50, pontoMedio.gety()-raio+12)); //noroeste
		pontos.add(new Ponto(pontoMedio.getx() - 100, pontoMedio.gety())); //oeste
		pontos.add(new Ponto(pontoMedio.getx() - 50, pontoMedio.gety()+raio-12)); //sudoeste
		pontos.add(new Ponto(pontoMedio.getx() + 50, pontoMedio.gety()+raio-12)); //sudeste
		return pontos;
	}

	private void desenharPontosDesenhoGeral(List<Ponto> pontos){
		for(Ponto ponto : pontos){
			this.desenhador.setCor(Color.BLUE);
			this.desenhador.desenharPonto((int) Math.floor(ponto.getx()), (int) Math.floor(ponto.gety()), "", desenhador.getCor());
		}
	}

	private List<Circulo> determinarCirculos(List<Ponto> pontos, int raio){
		List<Circulo> circulos = new ArrayList<>();
		for(Ponto ponto : pontos){
			circulos.add(new Circulo(raio, ponto, Color.GREEN));
		}
		return circulos;
	}

	private void desenharCirculosDesenhoGeral(List<Circulo> circulos){
		for(Circulo circulo : circulos){
			this.desenhador.setCor(Color.GREEN);
			this.desenhador.desenharPontos(CirculoCalculador.obterPontosAlgoritmoMidPoint(circulo), desenhador.getCor());
		}
	}

	private List<Reta> determinarRetasCirculoCentral(Ponto pontoMedio, List<Ponto> pontos){
		List<Reta> retas = new ArrayList<>();
		for(Ponto ponto : pontos){
			retas.add(new Reta(pontoMedio, ponto, Color.RED));
		}
		return retas;
	}

	private void desenharRetasDesenhoGeral(List<Reta> retas){
		for(Reta reta : retas){
			this.desenhador.setCor(Color.RED);
			this.desenhador.desenharPontos(RetaCalculador.obterPontos(reta), desenhador.getCor());
		}
	}

	private List<Ponto> determinarPontosExtremos(Ponto pontoMedio, int raio){
		List<Ponto> pontos = new ArrayList<>();
		pontos.add(new Ponto(pontoMedio.getx(), pontoMedio.gety() - 175)); //norte
		pontos.add(new Ponto(pontoMedio.getx() - 150, pontoMedio.gety()-raio+12)); //noroeste
		pontos.add(new Ponto(pontoMedio.getx() - 150, pontoMedio.gety()+raio-12)); //suldoeste
		pontos.add(new Ponto(pontoMedio.getx(), pontoMedio.gety() + 175)); //sul
		pontos.add(new Ponto(pontoMedio.getx() + 150, pontoMedio.gety()+raio-12)); //suldeste
		pontos.add(new Ponto(pontoMedio.getx() + 150, pontoMedio.gety()-raio+12)); //nordeste
		return pontos;
	}

	private List<Reta> determinarRetasExtremas(Ponto pontoMedio, List<Ponto> pontos){
		List<Reta> retas = new ArrayList<>();
		for(Ponto ponto : pontos){
			retas.add(new Reta(pontoMedio, ponto, Color.RED));
		}
		return retas;
	}

	private List<Reta> determinarRetasExtremas2(List<Ponto> pontos){
		List<Reta> retas = new ArrayList<>();

		retas.add(new Reta(pontos.get(0), pontos.get(5), Color.RED));
		retas.add(new Reta(pontos.get(1), pontos.get(0), Color.RED));
		retas.add(new Reta(pontos.get(2), pontos.get(1), Color.RED));
		retas.add(new Reta(pontos.get(3), pontos.get(2), Color.RED));
		retas.add(new Reta(pontos.get(4), pontos.get(3), Color.RED));
		retas.add(new Reta(pontos.get(5), pontos.get(4), Color.RED));

		retas.add(new Reta(pontos.get(0), pontos.get(4), Color.RED));
		retas.add(new Reta(pontos.get(4), pontos.get(2), Color.RED));
		retas.add(new Reta(pontos.get(2), pontos.get(0), Color.RED));

		retas.add(new Reta(pontos.get(3), pontos.get(5), Color.RED));
		retas.add(new Reta(pontos.get(5), pontos.get(1), Color.RED));
		retas.add(new Reta(pontos.get(1), pontos.get(3), Color.RED));

		return retas;
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
				case OPCAO_GERAL:
					desenharOpcaoGeral(pontoClicado);
					break;
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

	public void redesenhar() {
		canvas.getGraphicsContext2D().drawImage(backup,0,0);
		canvasLittle.getGraphicsContext2D().drawImage(backupLittle,0,0);
	}

	public void salvarCanvas(){
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.WHITE);
		this.backup = canvas.snapshot(params, backup);
		this.backupLittle = canvasLittle.snapshot(params, backupLittle);
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

	public void onFiltro(TipoFiltro filtro){
		//limpa canvas
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvasLittle.getGraphicsContext2D().clearRect(0, 0, canvasLittle.getWidth(), canvasLittle.getHeight());

		//seta nova cor nos objetos desenhados
		Map<TipoPrimitivo, List<Object>> objetosDesenhados = this.desenhador.getObjetosDesenhados();
		changeColor(objetosDesenhados, filtro);

		//atualiza objeto em desenhador, desenha objetos e salva
		this.getDesenhador().setObjetosDesenhados(objetosDesenhados);
		this.getDesenhador().desenharObjetosArmazenados(null);
		this.salvarCanvas();
	}

	private void changeColor(Map<TipoPrimitivo, List<Object>> objetosDesenhados, TipoFiltro filtro){

		objetosDesenhados.forEach((tipoPrimitivo, objetos) -> {
			for(Object desenho : objetos){
				Color cor = onPickColorOfFigura(tipoPrimitivo, desenho);
				cor = onChangeColorOfFigura(filtro, cor);
				onPutColorOfFigura(tipoPrimitivo, desenho, cor);
			}
		});

	}

	private Color onPickColorOfFigura(TipoPrimitivo tipoPrimitivo, Object desenho){
		Color cor;

		switch (tipoPrimitivo) {
			case RETA:
				Reta reta = (Reta) desenho;
				cor = reta.getCor() ;
				break;
			case RETANGULO:
				Retangulo retangulo = (Retangulo) desenho;
				cor = retangulo.getCor() ;
				break;
			case POLIGONO:
			case LINHA_POLIGONAL:
				Poligono poligono = (Poligono) desenho;
				cor = poligono.getCor() ;
				break;
			case CIRCULO:
				Circulo circulo = (Circulo) desenho;
				cor = circulo.getCor() ;
				break;
			default:
				cor = Color.WHITE;
				break;
		}

		return cor;
	}

	private Color onChangeColorOfFigura(TipoFiltro filtro, Color cor){
		if(filtro.equals(TipoFiltro.ALTA)) {
			return cor.saturate();
		} else if(filtro.equals(TipoFiltro.BAIXA)){
			return cor.desaturate();
		} else if(filtro.equals(TipoFiltro.INVERTE)){
			return cor.invert();
		} else if(filtro.equals(TipoFiltro.ILUMINAR)){
			return cor.brighter();
		} else if(filtro.equals(TipoFiltro.ESCURECER)){
			return cor.darker();
		}
		return cor.grayscale();
	};

	private void onPutColorOfFigura(TipoPrimitivo tipoPrimitivo, Object desenho, Color cor){
		switch (tipoPrimitivo) {
			case RETA:
				Reta reta = (Reta) desenho;
				reta.setCor(cor) ;
				break;
			case RETANGULO:
				Retangulo retangulo = (Retangulo) desenho;
				retangulo.setCor(cor) ;
				break;
			case POLIGONO:
			case LINHA_POLIGONAL:
				Poligono poligono = (Poligono) desenho;
				poligono.setCor(cor) ;
				break;
			case CIRCULO:
				Circulo circulo = (Circulo) desenho;
				circulo.setCor(cor) ;
				break;
		}
	}

}