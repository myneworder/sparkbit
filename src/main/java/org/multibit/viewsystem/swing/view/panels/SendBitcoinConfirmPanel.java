/* 
 * SparkBit
 *
 * Copyright 2011-2014 multibit.org
 * Copyright 2014 Coin Sciences Ltd
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit.viewsystem.swing.view.panels;

import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet.SendRequest;
import org.bitcoinj.wallet.Protos.Wallet.EncryptionType;
import org.sparkbit.SparkBit;
import org.multibit.controller.Controller;
import org.multibit.controller.bitcoin.BitcoinController;
import org.multibit.exchange.CurrencyConverter;
import org.multibit.model.bitcoin.BitcoinModel;
import org.multibit.model.bitcoin.WalletBusyListener;
import org.multibit.utils.ImageLoader;
import org.multibit.viewsystem.swing.ColorAndFontConstants;
import org.multibit.viewsystem.swing.MultiBitFrame;
import org.multibit.viewsystem.swing.action.CancelBackToParentAction;
import org.multibit.viewsystem.swing.action.OkBackToParentAction;
import org.multibit.viewsystem.swing.action.SendBitcoinNowAction;
import org.multibit.viewsystem.swing.view.components.MultiBitButton;
import org.multibit.viewsystem.swing.view.components.MultiBitDialog;
import org.multibit.viewsystem.swing.view.components.MultiBitLabel;
import org.multibit.viewsystem.swing.view.components.MultiBitTitledPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import org.coinspark.protocol.CoinSparkAddress;
import org.coinspark.protocol.CoinSparkMessagePart;

/* CoinSpark START */
// Get values from the asset validator map, rather than wallet prefs, if sending an asset
import org.multibit.viewsystem.swing.action.AssetValidator;
import org.coinspark.protocol.CoinSparkPaymentRef;
import org.multibit.model.bitcoin.WalletInfoData;
import org.multibit.utils.CSMiscUtils;
/* CoinSpark END */

/**
 * The send bitcoin confirm panel.
 */
public class SendBitcoinConfirmPanel extends JPanel implements WalletBusyListener {
    private static final long serialVersionUID = 191435612399957705L;

    private static final Logger log = LoggerFactory.getLogger(SendBitcoinConfirmPanel.class);

    private static final int STENT_WIDTH = 10;

    private MultiBitFrame mainFrame;
    private MultiBitDialog sendBitcoinConfirmDialog;

    private final Controller controller;
    private final BitcoinController bitcoinController;

    private MultiBitLabel sendAddressText;
    private MultiBitLabel sendLabelText;
    private MultiBitLabel sendAmountText;
    private MultiBitLabel sendFeeText;
    private MultiBitLabel sendMessageText;

    private String sendAddress;
    private String sendLabel;
    private SendRequest sendRequest;

    private MultiBitLabel confirmText1;
    private MultiBitLabel confirmText2;

    private SendBitcoinNowAction sendBitcoinNowAction;
    private MultiBitButton sendButton;
    private MultiBitButton cancelButton;

    private JPasswordField walletPasswordField;
    private MultiBitLabel walletPasswordPromptLabel;
    private MultiBitLabel explainLabel;

    private static SendBitcoinConfirmPanel thisPanel = null;

    private static ImageIcon shapeTriangleIcon;
    private static ImageIcon shapeSquareIcon;
    private static ImageIcon shapeHeptagonIcon;
    private static ImageIcon shapeHexagonIcon;
    private static ImageIcon progress0Icon;

    static {
        shapeTriangleIcon = ImageLoader.createImageIcon(ImageLoader.SHAPE_TRIANGLE_ICON_FILE);
        shapeSquareIcon = ImageLoader.createImageIcon(ImageLoader.SHAPE_SQUARE_ICON_FILE);
        shapeHeptagonIcon = ImageLoader.createImageIcon(ImageLoader.SHAPE_PENTAGON_ICON_FILE);
        shapeHexagonIcon = ImageLoader.createImageIcon(ImageLoader.SHAPE_HEXAGON_ICON_FILE);
        progress0Icon = ImageLoader.createImageIcon(ShowTransactionsPanel.PROGRESS_0_ICON_FILE);
    }

    /* CoinSpark Start */
    public void setIsAsset(boolean isAsset) {
	this.isAsset = isAsset;
    }

    public void setAssetDescription(String assetDescription) {
	this.assetDescription = assetDescription;
    }

    public void setAssetAmountLabel(String assetAmountLabel) {
	this.assetAmountLabel = assetAmountLabel;
    }
    
    public void setAssetValidator(AssetValidator assetValidator) {
	this.assetValidator = assetValidator;
    }

    public boolean isAsset() {
	return isAsset;
    }

    public String getAssetDescription() {
	return assetDescription;
    }

    public String getAssetAmountLabel() {
	return assetAmountLabel;
    }

    public AssetValidator getAssetValidator() {
	return assetValidator;
    }
    
    private boolean isAsset;
    private String assetDescription;
    private String assetAmountLabel;
    private AssetValidator assetValidator;
	    
    private MultiBitLabel sendAssetAmountText;
 
    private int yGridPositionMainPanel;
    private int yGridPositionDetailPanel;
    
    
    /* CoinSpark End */
    public SendBitcoinConfirmPanel(BitcoinController bitcoinController, MultiBitFrame mainFrame, MultiBitDialog sendBitcoinConfirmDialog, SendRequest sendRequest, String assetAmount, String assetDescription, AssetValidator validator) {
        super();
        this.bitcoinController = bitcoinController;
        this.controller = this.bitcoinController;
        this.mainFrame = mainFrame;
        this.sendBitcoinConfirmDialog = sendBitcoinConfirmDialog;
        this.sendRequest = sendRequest;
	
	setAssetAmountLabel(assetAmount);
	setAssetDescription(assetDescription);
	setAssetValidator(validator);
	setIsAsset(true);

        thisPanel = this;

        initUI();

        cancelButton.requestFocusInWindow();
        applyComponentOrientation(ComponentOrientation.getOrientation(controller.getLocaliser().getLocale()));

        this.bitcoinController.registerWalletBusyListener(this);
    }
    
    /**
     * Creates a new {@link SendBitcoinConfirmPanel}.
     */
    public SendBitcoinConfirmPanel(BitcoinController bitcoinController, MultiBitFrame mainFrame, MultiBitDialog sendBitcoinConfirmDialog, SendRequest sendRequest) {
        super();
        this.bitcoinController = bitcoinController;
        this.controller = this.bitcoinController;
        this.mainFrame = mainFrame;
        this.sendBitcoinConfirmDialog = sendBitcoinConfirmDialog;
        this.sendRequest = sendRequest;

        thisPanel = this;

        initUI();

        cancelButton.requestFocusInWindow();
        applyComponentOrientation(ComponentOrientation.getOrientation(controller.getLocaliser().getLocale()));

        this.bitcoinController.registerWalletBusyListener(this);
    }

    // Cleanup
    public void cleanUp() {
//	log.debug(">>>> cleanUp() invoked");
	if (sendBitcoinNowAction!=null) sendBitcoinNowAction.cleanUp();
    }
    
    
    /**
     * Initialise bitcoin confirm panel.
     */
    public void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.setLayout(new GridBagLayout());

        String[] keys = new String[] { "sendBitcoinPanel.addressLabel",
                "sendBitcoinPanel.labelLabel", "sendBitcoinPanel.amountLabel",
                "showPreferencesPanel.feeLabel.text", "showExportPrivateKeysPanel.walletPasswordPrompt"};

        int stentWidth = MultiBitTitledPanel.calculateStentWidthForKeys(controller.getLocaliser(), keys, mainPanel)
                + ExportPrivateKeysPanel.STENT_DELTA;

        // Get the data out of the wallet preferences.
        sendAddress = this.bitcoinController.getModel().getActiveWalletPreference(BitcoinModel.SEND_ADDRESS);
        sendLabel = this.bitcoinController.getModel().getActiveWalletPreference(BitcoinModel.SEND_LABEL);
        String sendAmount = this.bitcoinController.getModel().getActiveWalletPreference(BitcoinModel.SEND_AMOUNT) + " " + controller.getLocaliser(). getString("sendBitcoinPanel.amountUnitLabel");
	
	/* CoinSpark START */
	// Note: This method initUI() is invoked in the panel constructor, so set up asset related variables early
	String sendAssetAmountLocalised = "";
	if (isAsset()) {
	    // TODO: Not localised yet.
	    sendAssetAmountLocalised = getAssetAmountLabel() + " " + getAssetDescription();
	    
	    // We know that sendAddress is the coinspark address, since we've validated, so let's show that.
	    // We can also show the underlying bitcoin address too.
	    //sendAddress = sendAddress + ", bitcoin=" + this.assetValidator.getState(AssetValidator.VALIDATION_ADDRESS_VALUE);
	    
	    sendAmount = this.assetValidator.getState(AssetValidator.VALIDATION_AMOUNT_VALUE) + " " + controller.getLocaliser(). getString("sendBitcoinPanel.amountUnitLabel");
	}
	/* CoinSpark END */
	
	
	

        String sendAmountLocalised = CurrencyConverter.INSTANCE.prettyPrint(sendAmount);

        String fee = "0";
        if (sendRequest != null) {
            fee = Utils.bitcoinValueToPlainString(sendRequest.fee);
        }

        String sendFeeLocalised = CurrencyConverter.INSTANCE.prettyPrint(fee);

	yGridPositionMainPanel = 0;
	yGridPositionDetailPanel = 0;
	
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(MultiBitTitledPanel.createStent(STENT_WIDTH), constraints);


        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 2;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(MultiBitTitledPanel.createStent(STENT_WIDTH, STENT_WIDTH), constraints);

	
	yGridPositionMainPanel++;
	
	
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 7;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(MultiBitTitledPanel.createStent(STENT_WIDTH), constraints);

        explainLabel = new MultiBitLabel("");
	/* CoinSpark START */
        explainLabel.setText(controller.getLocaliser().getString( isAsset() ? "sendAssetConfirmView.message" : "sendBitcoinConfirmView.message"));
	/* CoinSpark END */
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.8;
        constraints.weighty = 0.4;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(explainLabel, constraints);
        mainPanel.add(MultiBitTitledPanel.createStent(explainLabel.getPreferredSize().width, explainLabel.getPreferredSize().height), constraints);
	
	
	yGridPositionMainPanel++;
	
	
        ImageIcon bigIcon = ImageLoader.createImageIcon(ImageLoader.MULTIBIT_128_ICON_FILE);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.5;
        constraints.weighty = 0.2;
        constraints.gridwidth = 1;
        constraints.gridheight = 5;
        constraints.anchor = GridBagConstraints.CENTER;
        JLabel bigIconLabel = new JLabel(bigIcon);
        mainPanel.add(bigIconLabel, constraints);

	
	
        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBackground(ColorAndFontConstants.VERY_LIGHT_BACKGROUND_COLOR);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.6;
        constraints.weighty = 0.8;
        constraints.gridwidth = 3;
        constraints.gridheight = 5;
        constraints.anchor = GridBagConstraints.CENTER;
        mainPanel.add(detailPanel, constraints);

	// GridHeight here is 5, not 1
	yGridPositionMainPanel += constraints.gridheight;

	
        GridBagConstraints constraints2 = new GridBagConstraints();

        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.gridx = 0;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.05;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_START;
        detailPanel.add(MultiBitTitledPanel.createStent(stentWidth), constraints2);

        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.gridx = 1;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.05;
        constraints2.weighty = 0.05;
        constraints2.gridwidth = 1;
        constraints2.gridheight = 1;
        constraints2.anchor = GridBagConstraints.CENTER;
        detailPanel.add(MultiBitTitledPanel.createStent(MultiBitTitledPanel.SEPARATION_BETWEEN_NAME_VALUE_PAIRS),
                constraints2);

        JLabel forcer1 = new JLabel();
        forcer1.setOpaque(false);
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.gridx = 2;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 10;
        constraints2.weighty = 0.05;
        constraints2.gridwidth = 1;
        constraints2.gridheight = 1;
        constraints2.anchor = GridBagConstraints.LINE_END;
        detailPanel.add(forcer1, constraints2);

	
	yGridPositionDetailPanel++;
	
	
        MultiBitLabel sendAddressLabel = new MultiBitLabel("");
        sendAddressLabel.setText(controller.getLocaliser().getString("sendBitcoinPanel.addressLabel"));
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 0;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_END;
        detailPanel.add(sendAddressLabel, constraints2);

	String sendAddressTextLabel = sendAddress;
	
	// If the address belongs to the same wallet as the sending address, alert user.
	WalletInfoData wid = this.bitcoinController.getModel().getActiveWalletWalletInfo();
	String sameWalletAddress = sendAddress;
	if (sendAddress.startsWith("s")) {
	    CoinSparkAddress csa = CSMiscUtils.decodeCoinSparkAddress(sendAddress);
	    if (csa != null) {
		sameWalletAddress = csa.getBitcoinAddress();
	    } else {
		sameWalletAddress = null;
	    }
	}
	if (sameWalletAddress!=null) {
	    boolean b = wid.containsReceivingAddress(sameWalletAddress);
	    if (b) {
		sendAddressTextLabel = sendAddressTextLabel + " (...belongs to sending wallet)";
	    }
	}
	
        sendAddressText = new MultiBitLabel("");
        sendAddressText.setText(sendAddressTextLabel);
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 2;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_START;
        detailPanel.add(sendAddressText, constraints2);
	
	
	
	yGridPositionDetailPanel++;
	
	
	
	// Show payment reference if it exists and is not 0
	CoinSparkPaymentRef paymentRef = null;
	paymentRef = sendRequest.getPaymentRef();
	if (paymentRef!=null) {
	    long x = paymentRef.getRef();
	    if (x > 0) {
		MultiBitLabel paymentRefLabel = new MultiBitLabel("");
		paymentRefLabel.setText("Payment Reference:");
			//controller.getLocaliser().getString("sendBitcoinPanel.addressLabel"));
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.gridx = 0;
		constraints2.gridy = yGridPositionDetailPanel;
		constraints2.weightx = 0.3;
		constraints2.weighty = 0.1;
		constraints2.gridwidth = 1;
		constraints2.anchor = GridBagConstraints.LINE_END;
		detailPanel.add(paymentRefLabel, constraints2);

		MultiBitLabel paymentRefText = new MultiBitLabel("");
		paymentRefText.setText("" + x);
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.gridx = 2;
		constraints2.gridy = yGridPositionDetailPanel;
		constraints2.weightx = 0.3;
		constraints2.weighty = 0.1;
		constraints2.gridwidth = 1;
		constraints2.anchor = GridBagConstraints.LINE_START;
		detailPanel.add(paymentRefText, constraints2);

		
		yGridPositionDetailPanel++;
	    }
	}


	// Show extract of message
	CoinSparkMessagePart[] messageParts = sendRequest.getMessageParts();
	if (messageParts != null && messageParts.length > 0) {
	    // As convention, SparkBit will place the message at index 0 and attachments afterwards.
	    // NOTE: This might change in future
	    CoinSparkMessagePart part = messageParts[0];
	    if (part.mimeType.equals("text/plain") && part.fileName == null) {
		String message = null;
		try {
		    message = new String(part.content, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
		    message = "Error decoding message, please check it!";
		}
		// Replace significant white space in our preview
		String messageToShow = message.replace("\n\n", " ").replace("\n", " ").replace("\t", " ");
		final int messageLen = messageToShow.length();
		final int messageDisplayLimit = 60;
		if (messageLen > messageDisplayLimit) {
		    messageToShow = messageToShow.substring(0, messageDisplayLimit) + "...(" + (messageLen - messageDisplayLimit) + " more characters)";
		}
		MultiBitLabel messageLabel = new MultiBitLabel("");
		messageLabel.setText(controller.getLocaliser().getString("sendBitcoinPanel.messageLabel"));
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.gridx = 0;
		constraints2.gridy = yGridPositionDetailPanel;
		constraints2.weightx = 0.3;
		constraints2.weighty = 0.1;
		constraints2.gridwidth = 1;
		constraints2.anchor = GridBagConstraints.LINE_END;
		detailPanel.add(messageLabel, constraints2);

		sendMessageText = new MultiBitLabel("");
		sendMessageText.setText(messageToShow);
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.gridx = 2;
		constraints2.gridy = yGridPositionDetailPanel;
		constraints2.weightx = 0.3;
		constraints2.weighty = 0.1;
		constraints2.gridwidth = 1;
		constraints2.anchor = GridBagConstraints.LINE_START;
		detailPanel.add(sendMessageText, constraints2);
	    }
	}
	
	yGridPositionDetailPanel++;
	
	
        MultiBitLabel sendLabelLabel = new MultiBitLabel("");
        sendLabelLabel.setText(controller.getLocaliser().getString("sendBitcoinPanel.labelLabel"));
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 0;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_END;
        detailPanel.add(sendLabelLabel, constraints2);

        sendLabelText = new MultiBitLabel("");
        sendLabelText.setText(sendLabel);
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 2;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_START;
        detailPanel.add(sendLabelText, constraints2);

	
	yGridPositionDetailPanel++;
	
	
	/* CoinSpark START */
	if (isAsset()) {
	    MultiBitLabel sendAssetAmountLabel = new MultiBitLabel("");
	    sendAssetAmountLabel.setText(controller.getLocaliser().getString("sendAssetPanel.assetAmountLabel"));
	    constraints2.fill = GridBagConstraints.NONE;
	    constraints2.gridx = 0;
	    constraints2.gridy = yGridPositionDetailPanel;
	    constraints2.weightx = 0.3;
	    constraints2.weighty = 0.1;
	    constraints2.gridwidth = 1;
	    constraints2.anchor = GridBagConstraints.LINE_END;
	    detailPanel.add(sendAssetAmountLabel, constraints2);

	    sendAssetAmountText = new MultiBitLabel("");
	    sendAssetAmountText.setText(sendAssetAmountLocalised);
	    constraints2.fill = GridBagConstraints.NONE;
	    constraints2.gridx = 2;
	    constraints2.gridy = yGridPositionDetailPanel;
	    constraints2.weightx = 0.3;
	    constraints2.weighty = 0.1;
	    constraints2.gridwidth = 1;
	    constraints2.anchor = GridBagConstraints.LINE_START;
	    detailPanel.add(sendAssetAmountText, constraints2);
	    
	    
	    yGridPositionDetailPanel++;
	    
	    
	}
	/* CoinSpark END */
	
        MultiBitLabel sendAmountLabel = new MultiBitLabel("");
	sendAmountLabel.setText(controller.getLocaliser().getString("sendAssetPanel.btcAmountLabel")); // CoinSpark
        //sendAmountLabel.setText(controller.getLocaliser().getString("sendBitcoinPanel.amountLabel"));
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 0;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_END;
        detailPanel.add(sendAmountLabel, constraints2);

        sendAmountText = new MultiBitLabel("");
        sendAmountText.setText(sendAmountLocalised);
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 2;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_START;
        detailPanel.add(sendAmountText, constraints2);

	
	yGridPositionDetailPanel++;
	
	
        MultiBitLabel sendFeeLabel = new MultiBitLabel("");
        sendFeeLabel.setText(controller.getLocaliser().getString("sendBitcoinPanel.feeLabel"));
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 0;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_END;
        detailPanel.add(sendFeeLabel, constraints2);

        sendFeeText = new MultiBitLabel("");
        sendFeeText.setText(sendFeeLocalised);
        constraints2.fill = GridBagConstraints.NONE;
        constraints2.gridx = 2;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.1;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_START;
        detailPanel.add(sendFeeText, constraints2);

	
	yGridPositionDetailPanel++;
	
	
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.gridx = 0;
        constraints2.gridy = yGridPositionDetailPanel;
        constraints2.weightx = 0.3;
        constraints2.weighty = 0.05;
        constraints2.gridwidth = 1;
        constraints2.anchor = GridBagConstraints.LINE_START;
        detailPanel.add(MultiBitTitledPanel.createStent(stentWidth), constraints2);

	
	yGridPositionDetailPanel++;
	
	
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel; // y value used to be 8
        constraints.weightx = 0.3;
        constraints.weighty = 0.3;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(MultiBitTitledPanel.createStent(stentWidth), constraints);
	

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 4;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.05;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        mainPanel.add(MultiBitTitledPanel.createStent(MultiBitTitledPanel.SEPARATION_BETWEEN_NAME_VALUE_PAIRS),
                constraints);

        JLabel forcer2 = new JLabel();
        forcer2.setOpaque(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 5;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 10;
        constraints.weighty = 0.05;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(forcer2, constraints);

        JPanel filler4 = new JPanel();
        filler4.setOpaque(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.01;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler4, constraints);


	yGridPositionMainPanel++;

	
        walletPasswordField = new JPasswordField(24);
        walletPasswordField.setMinimumSize(new Dimension(200, 20));
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 5;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(walletPasswordField, constraints);
        mainPanel.add(MultiBitTitledPanel.createStent(200, 20), constraints);

	        // Add wallet password field.
        walletPasswordPromptLabel = new MultiBitLabel(controller.getLocaliser().getString("sendBitcoinPanel.walletPasswordPromptLabel"));
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(walletPasswordPromptLabel, constraints);
        mainPanel.add(MultiBitTitledPanel.createStent(walletPasswordPromptLabel.getPreferredSize().width, walletPasswordPromptLabel.getPreferredSize().height), constraints);

	
	
	yGridPositionMainPanel++;
	
	
	
        JPanel filler5 = new JPanel();
        filler4.setOpaque(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.3;
        constraints.weighty = 0.01;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler5, constraints);

        if (this.bitcoinController.getModel().getActiveWallet() != null) {
            if (this.bitcoinController.getModel().getActiveWallet().getEncryptionType() == EncryptionType.ENCRYPTED_SCRYPT_AES) {
                // Need wallet password.
                walletPasswordField.setEnabled(true);
                walletPasswordPromptLabel.setEnabled(true);
            } else {
                // No wallet password required.
                walletPasswordField.setEnabled(false);
                walletPasswordPromptLabel.setEnabled(false);
            }
        }

	
	yGridPositionMainPanel++;
	
	
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 3;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.8;
        constraints.weighty = 0.1;
        constraints.gridwidth = 4;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(buttonPanel, constraints);

        CancelBackToParentAction cancelAction = new CancelBackToParentAction(controller, ImageLoader.createImageIcon(ImageLoader.CROSS_ICON_FILE), sendBitcoinConfirmDialog);
        cancelButton = new MultiBitButton(cancelAction, controller);
        buttonPanel.add(cancelButton);

        sendBitcoinNowAction = new SendBitcoinNowAction(mainFrame, this.bitcoinController, this, walletPasswordField, ImageLoader.createImageIcon(ImageLoader.SEND_BITCOIN_ICON_FILE), sendRequest, this.isAsset()); // CoinSpark: added last boolean parameter.
        sendButton = new MultiBitButton(sendBitcoinNowAction, controller);
        buttonPanel.add(sendButton);

	
	yGridPositionMainPanel++;
	
	
        confirmText1 = new MultiBitLabel("");
        confirmText1.setText(" ");
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.8;
        constraints.weighty = 0.15;
        constraints.gridwidth = 6;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(confirmText1, constraints);

        JLabel filler3 = new JLabel();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 7;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.05;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler3, constraints);

	
	yGridPositionMainPanel++;
	
	
        confirmText2 = new MultiBitLabel(" ");
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.8;
        constraints.weighty = 0.15;
        constraints.gridwidth = 6;
        constraints.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(confirmText2, constraints);

        JLabel filler6 = new JLabel();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 7;
        constraints.gridy = yGridPositionMainPanel;
        constraints.weightx = 0.05;
        constraints.weighty = 0.1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(filler6, constraints);

        enableSendAccordingToNumberOfConnectedPeersAndWalletBusy();
    }

    private void enableSendAccordingToNumberOfConnectedPeersAndWalletBusy() {
        boolean enableSend = false;
        String message = " ";

        if (this.controller.getModel() != null) {
            String singleNodeConnection = this.controller.getModel().getUserPreference(BitcoinModel.SINGLE_NODE_CONNECTION);
            boolean singleNodeConnectionOverride = singleNodeConnection != null && singleNodeConnection.trim().length() > 0;

            String peers = this.controller.getModel().getUserPreference(BitcoinModel.PEERS);
            boolean singlePeerOverride = peers != null && peers.split(",").length == 1;

            if (thisPanel.sendBitcoinNowAction != null) {
                if (!singleNodeConnectionOverride && !singlePeerOverride && this.bitcoinController.getModel().getNumberOfConnectedPeers() < BitcoinModel.MINIMUM_NUMBER_OF_CONNECTED_PEERS_BEFORE_SEND_IS_ENABLED) {
                     // Disable send button
                    enableSend = false;
                    message = controller.getLocaliser().getString("sendBitcoinConfirmView.multibitMustBeOnline");
                } else {
                    // Enable send button
                    enableSend = true;
                    message = " ";
                }
                if (this.bitcoinController.getModel().getActivePerWalletModelData().isBusy()) {
                    enableSend = false;
                    message = controller.getLocaliser().getString("multiBitSubmitAction.walletIsBusy",
                            new Object[]{controller.getLocaliser().getString(this.bitcoinController.getModel().getActivePerWalletModelData().getBusyTaskKey())});
                }
                thisPanel.sendBitcoinNowAction.setEnabled(enableSend);
            }
        }

        if (sendBitcoinNowAction != null) {
            sendBitcoinNowAction.setEnabled(enableSend);
            if (confirmText1 != null) {
                if (enableSend) {
                    // Only clear the 'multibitMustBeOnline' message.
                    if (controller.getLocaliser().getString("sendBitcoinConfirmView.multibitMustBeOnline").equals(confirmText1.getText())) {
                        confirmText1.setText(message);
                    }
                } else {
                    confirmText1.setText(message);
                }
            }
        }
    }

    public void setMessageText(final String message1) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                confirmText1.setText(message1);
            }});
        invalidate();
        validate();
        repaint();
    }

    public void setMessageText(final String message1, final String message2) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                confirmText1.setText(message1);
                confirmText2.setText(" " + message2);
            }});
        invalidate();
        validate();
        repaint();
    }

    public void clearAfterSend() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                walletPasswordField.setText("");
                walletPasswordField.setVisible(false);
                explainLabel.setVisible(false);
                walletPasswordPromptLabel.setVisible(false);
            }});
    }

    public void showOkButton() {
        OkBackToParentAction okAction = new OkBackToParentAction(controller, sendBitcoinConfirmDialog);
        sendButton.setAction(okAction);
	
	// Fudge UI...
	if (isAsset()) {
	    Runnable r = new Runnable() {
		@Override
		public void run() {
		    mainFrame.sendPanelTransaction = null;
		}
	    };
	    okAction.setRunnableToRunOnExit(r);
	}
	
        cancelButton.setVisible(false);
    }

    public static void updatePanel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (thisPanel != null && thisPanel.isVisible()) {
                    final BitcoinController bitcoinController = SparkBit.getBitcoinController();
                    if (bitcoinController != null) {
                        String singleNodeConnection = bitcoinController.getModel().getUserPreference(BitcoinModel.SINGLE_NODE_CONNECTION);
                        boolean singleNodeConnectionOverride = singleNodeConnection != null && singleNodeConnection.trim().length() > 0;

                        String peers = bitcoinController.getModel().getUserPreference(BitcoinModel.PEERS);
                        boolean singlePeerOverride = peers != null && peers.split(",").length == 1;

                        boolean enableSend = false;
                        if (thisPanel.sendBitcoinNowAction != null) {
                            if (!singleNodeConnectionOverride && !singlePeerOverride && bitcoinController.getModel().getNumberOfConnectedPeers() < BitcoinModel.MINIMUM_NUMBER_OF_CONNECTED_PEERS_BEFORE_SEND_IS_ENABLED) {
                                // Disable send button
                                enableSend = false;
                            } else {
                                // Enable send button
                                enableSend = true;
                            }
                            if (bitcoinController.getModel().getActivePerWalletModelData().isBusy()) {
                                enableSend = false;
                            }
                            thisPanel.sendBitcoinNowAction.setEnabled(enableSend);
                        }

                        MultiBitLabel confirmText1 = thisPanel.confirmText1;
                        if (enableSend) {
                            if (confirmText1 != null) {
                                if (SparkBit.getController().getLocaliser()
                                        .getString("sendBitcoinConfirmView.multibitMustBeOnline").equals(confirmText1.getText())) {
                                    confirmText1.setText(" ");
                                }
                            }
                        } else {
                            if (confirmText1 != null) {
                                confirmText1.setText(SparkBit.getController().getLocaliser()
                                        .getString("sendBitcoinConfirmView.multibitMustBeOnline"));
                            }
                        }
                    }

                    thisPanel.invalidate();
                    thisPanel.validate();
                    thisPanel.repaint();
                }
            }
        });
    }

    public static void updatePanelDueToTransactionConfidenceChange(final Sha256Hash transactionWithChangedConfidenceHash,
            final int numberOfPeersSeenBy) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (thisPanel == null || !thisPanel.isVisible() || thisPanel.getSendBitcoinNowAction() == null) {
                    return;
                }
                Transaction sentTransaction = thisPanel.getSendBitcoinNowAction().getTransaction();

                if (sentTransaction == null || !sentTransaction.getHash().equals(transactionWithChangedConfidenceHash)) {
                    return;
                }

                MultiBitLabel confirmText2 = thisPanel.getConfirmText2();
                if (confirmText2 != null) {
                    confirmText2.setText(thisPanel.getConfidenceToolTip(numberOfPeersSeenBy));
                    confirmText2.setIcon(thisPanel.getConfidenceIcon(numberOfPeersSeenBy));
                }

                thisPanel.invalidate();
                thisPanel.validate();
                thisPanel.repaint();
            }
        });
    }
    private String getConfidenceToolTip(int numberOfPeers) {
        StringBuilder builder = new StringBuilder("");
            builder
                .append(SparkBit.getController().getLocaliser().getString("transactionConfidence.seenBy"))
                .append(" ");
            builder.append(numberOfPeers);
            if (numberOfPeers == 1) {
              builder.append(" ")
                  .append(SparkBit.getController().getLocaliser().getString("transactionConfidence.peer"))
                  .append(".");
            } else {
              builder
                    .append(" ")
                    .append(SparkBit.getController().getLocaliser().getString("transactionConfidence.peers"))
                    .append(".");
            }
        return builder.toString();
    }

    private ImageIcon getConfidenceIcon(int numberOfPeers) {
        // By default return a triangle which indicates the least known.
        ImageIcon iconToReturn;

        if (numberOfPeers >= 4) {
            return progress0Icon;
        } else {
            switch (numberOfPeers) {
            case 0:
                iconToReturn = shapeTriangleIcon;
                break;
            case 1:
                iconToReturn = shapeSquareIcon;
                break;
            case 2:
                iconToReturn = shapeHeptagonIcon;
                break;
            case 3:
                iconToReturn = shapeHexagonIcon;
                break;
            default:
                iconToReturn = shapeTriangleIcon;
            }
        }
        return iconToReturn;
    }

    public MultiBitButton getCancelButton() {
        return cancelButton;
    }

    // Used in testing.
    public SendBitcoinNowAction getSendBitcoinNowAction() {
        return sendBitcoinNowAction;
    }

    public String getMessageText1() {
        return confirmText1.getText();
    }

    public String getMessageText2() {
        return confirmText2.getText();
    }

    public void setWalletPassword(CharSequence password) {
        walletPasswordField.setText(password.toString());
    }

    public boolean isWalletPasswordFieldEnabled() {
        return walletPasswordField.isEnabled();
    }

    public MultiBitLabel getConfirmText2() {
        return confirmText2;
    }

    @Override
    public void walletBusyChange(boolean newWalletIsBusy) {
        enableSendAccordingToNumberOfConnectedPeersAndWalletBusy();
    }
}
