package de.breitbandig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.graphics.DrawObject;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GraphicOnTextStripperStreamEngine extends PDFGraphicsStreamEngine {

    private static final Log LOG = LogFactory.getLog(GraphicOnTextStripperStreamEngine.class);

    private final EnhancedTextStripper stripper;

    private Method initPageMethod;

    public GraphicOnTextStripperStreamEngine(EnhancedTextStripper stripper, PDPage page) {
        super(page);

        this.stripper = stripper;

        try {
            hacks();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private void hacks() throws ReflectiveOperationException {
        initPageMethod = PDFStreamEngine.class.getDeclaredMethod("initPage", new Class[] { PDPage.class });
        initPageMethod.setAccessible(true);
    }

    public void hackInitPage(PDPage page) {
        try {
            initPageMethod.invoke(this, page);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }



    @Override
    public PDResources getResources() {
        return stripper.getResources();
    }

    /*
    @Override
    public void endArticle() throws IOException {
        stripper.endArticle();
    }

    @Override
    public void endPage(PDPage page) throws IOException {
        stripper.endPage(page);
    }
     */

    @Override
    public void endText() throws IOException {
        stripper.endText();
    }

    @Override
    public void processAnnotation(PDAnnotation annotation, PDAppearanceStream appearance) throws IOException {
        stripper.processAnnotation(annotation, appearance);
    }

    @Override
    public void processChildStream(PDContentStream contentStream, PDPage page) throws IOException {
        stripper.processChildStream(contentStream, page);
    }

    @Override
    public void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        stripper.processOperator(operator, operands);
    }

    @Override
    public void processOperator(String operator, List<COSBase> operands) throws IOException {
        stripper.processOperator(operator, operands);
    }

    @Override
    public void processPage(PDPage page) throws IOException {
        // ???
        hackInitPage(page);

        stripper.processPage(page);
    }

    @Override
    public void processSoftMask(PDTransparencyGroup group) throws IOException {
        stripper.processSoftMask(group);
    }

    /*
    @Override
    public void processTextPosition(TextPosition pos) throws IOException {
        stripper.processTextPosition(pos);
    }
     */

    @Override
    public void processTransparencyGroup(PDTransparencyGroup group) throws IOException {
        stripper.processTransparencyGroup(group);
    }

    /*
    @Override
    public void startArticle() throws IOException {
        stripper.startArticle();
    }

    @Override
    public void startArticle(boolean isLTR) throws IOException {
        stripper.startArticle(isLTR);
    }

    @Override
    public void startPage(PDPage page) throws IOException {
        stripper.startPage(page);
    }
     */

    @Override
    public void saveGraphicsState()
    {
        stripper.saveGraphicsState();
    }

    @Override
    public void restoreGraphicsState()
    {
        stripper.restoreGraphicsState();
    }

    @Override
    public int getGraphicsStackSize()
    {
        return stripper.getGraphicsStackSize();
    }

    @Override
    public PDGraphicsState getGraphicsState()
    {
        return stripper.getGraphicsState();
    }

    @Override
    public Matrix getTextLineMatrix()
    {
        return stripper.getTextLineMatrix();
    }

    @Override
    public void setTextLineMatrix(Matrix value)
    {
        stripper.setTextLineMatrix(value);
    }

    @Override
    public Matrix getTextMatrix()
    {
        return stripper.getTextMatrix();
    }

    @Override
    public void setTextMatrix(Matrix value)
    {
        stripper.setTextMatrix(value);
    }

    @Override
    public void setLineDashPattern(COSArray array, int phase)
    {
        stripper.setLineDashPattern(array, phase);
    }

    /*
    @Override
    public PDResources getResources()
    {
        return resources;
    }
     */

    @Override
    public PDPage getCurrentPage()
    {
        return stripper.getCurrentPage();
    }

    @Override
    public Matrix getInitialMatrix()
    {
        return stripper.getInitialMatrix();
    }

    @Override
    public Point2D.Float transformedPoint(float x, float y)
    {
        return stripper.transformedPoint(x, y);
    }

    /*
    @Override
    protected float transformWidth(float width)
    {
        // TODO (tp): ???
        return width;
    }
     */



    @Override
    public void showTextString(byte[] string) throws IOException
    {
        stripper.showTextString(string);
    }

    @Override
    public void showTextStrings(COSArray array) throws IOException
    {
        stripper.showTextStrings(array);
    }

    /*
    @Override
    protected void applyTextAdjustment(float tx, float ty) throws IOException
    {
        stripper.apply
    }

    @Override
    protected void showText(byte[] string) throws IOException
    {
        stripper.showText()
    }

    @Override
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                             Vector displacement) throws IOException
    {
        stripper.showG
    }

    @Override
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
    {
    }

    @Override
    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code,
                                  String unicode, Vector displacement) throws IOException
    {
    }
     */



    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
        LOG.info("appendRectangle");
    }

    public void drawImage(PDImage pdImage) throws IOException {
        LOG.info("drawImage");
    }

    public void clip(int windingRule) throws IOException {
        LOG.info("clip");
    }

    public void moveTo(float x, float y) throws IOException {
        LOG.info("moveTo");
    }

    public void lineTo(float x, float y) throws IOException {
        LOG.info("lineTo");
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        LOG.info("curveTo");
    }

    public Point2D getCurrentPoint() throws IOException {
        LOG.info("getCurrentPoint");
        return null;
    }

    public void closePath() throws IOException {
        LOG.info("closePath");
    }

    public void endPath() throws IOException {
        LOG.info("endPath");
    }

    public void strokePath() throws IOException {
        LOG.info("strokePath");
    }

    public void fillPath(int windingRule) throws IOException {
        LOG.info("fillPath");
    }

    public void fillAndStrokePath(int windingRule) throws IOException {
        LOG.info("fillAndStrokePath");
    }

    public void shadingFill(COSName shadingName) throws IOException {
        LOG.info("shadingFill");
    }
}
