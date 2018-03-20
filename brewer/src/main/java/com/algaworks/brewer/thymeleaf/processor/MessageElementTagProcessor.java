package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class MessageElementTagProcessor extends AbstractElementTagProcessor{

	private static final String NOME_TAG = "message"; // <brewer:message/>
	private static final int PRECEDENCIA = 1000;
	
	public MessageElementTagProcessor(String dialectPrefix){
		super(TemplateMode.HTML, dialectPrefix, NOME_TAG, true, null, false, PRECEDENCIA);
	}
	
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {

		IModelFactory modelFactory = context.getModelFactory();
		
		IModel model = modelFactory.createModel();
		model.add(modelFactory.createStandaloneElementTag("th:block", "th:replace", "fragments/MensagemSucesso :: alert"));
		model.add(modelFactory.createStandaloneElementTag("th:block", "th:replace", "fragments/MensagensErroValidacao :: alert"));
		
	    /**
	     * @param model the model to be used as a replacement.
	     * @param processable whether the model should be considered <em>processable</em> or not.  Se será processado pelo spring ou não.
	     * 
	        public void replaceWith(final IModel model, final boolean processable);
	     */
		structureHandler.replaceWith(model, true);  
		
	}

}
