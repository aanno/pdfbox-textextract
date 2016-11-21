package de.breitbandig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.contentstream.operator.graphics.*;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.contentstream.operator.text.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class EnhancedTextStripper extends PDFTextStripper {

    private static final Log LOG = LogFactory.getLog(EnhancedTextStripper.class);

    private final StringBuilder part = new StringBuilder();

    private final DrawObject doo = new DrawObject();

    private Map<String, OperatorProcessor> operators;

    private GraphicOnTextStripperStreamEngine gse;

    private TextPosition last;

    public EnhancedTextStripper() throws IOException {
        // use the NORMAL DrawObject, i.e. override special text-only version (tp)
        // addOperator(doo);

        gse = new GraphicOnTextStripperStreamEngine(this, new PDPage());
        try {
            hackAddOperator(doo);

            // Graphics
            addOp(new CloseFillNonZeroAndStrokePath());
            addOp(new FillNonZeroAndStrokePath());
            addOp(new CloseFillEvenOddAndStrokePath());
            addOp(new FillEvenOddAndStrokePath());
            addOp(new BeginInlineImage());
            addOp2(new BeginText());
            addOp(new CurveTo());
            addOp2(new Concatenate());
            addOp2(new SetStrokingColorSpace());
            addOp2(new SetNonStrokingColorSpace());
            addOp2(new SetLineDashPattern());
            addOp(new DrawObject()); // special graphics version
            addOp2(new EndText());
            addOp(new FillNonZeroRule());
            addOp(new LegacyFillNonZeroRule());
            addOp(new FillEvenOddRule());
            addOp2(new SetStrokingDeviceGrayColor());
            addOp2(new SetNonStrokingDeviceGrayColor());
            addOp2(new SetGraphicsStateParameters());
            addOp(new ClosePath());
            addOp2(new SetFlatness());
            addOp2(new SetLineJoinStyle());
            addOp2(new SetLineCapStyle());
            addOp2(new SetStrokingDeviceCMYKColor());
            addOp2(new SetNonStrokingDeviceCMYKColor());
            addOp(new LineTo());
            addOp(new MoveTo());
            addOp2(new SetLineMiterLimit());
            addOp(new EndPath());
            addOp2(new Save());
            addOp2(new Restore());
            addOp(new AppendRectangleToPath());
            addOp2(new SetStrokingDeviceRGBColor());
            addOp2(new SetNonStrokingDeviceRGBColor());
            addOp2(new SetRenderingIntent());
            addOp(new CloseAndStrokePath());
            addOp(new StrokePath());
            addOp2(new SetStrokingColor());
            addOp2(new SetNonStrokingColor());
            addOp2(new SetStrokingColorN());
            addOp2(new SetNonStrokingColorN());
            addOp(new ShadingFill());
            addOp2(new NextLine());
            addOp2(new SetCharSpacing());
            addOp2(new MoveText());
            addOp2(new MoveTextSetLeading());
            addOp2(new SetFontAndSize());
            addOp2(new ShowText());
            addOp2(new ShowTextAdjusted());
            addOp2(new SetTextLeading());
            addOp2(new SetMatrix());
            addOp2(new SetTextRenderingMode());
            addOp2(new SetTextRise());
            addOp2(new SetWordSpacing());
            addOp2(new SetTextHorizontalScaling());
            addOp(new CurveToReplicateInitialPoint());
            addOp2(new SetLineWidth());
            addOp(new ClipNonZeroRule());
            addOp(new ClipEvenOddRule());
            addOp(new CurveToReplicateFinalPoint());
            addOp2(new ShowTextLine());
            addOp2(new ShowTextLineAndSpace());
        } catch (ReflectiveOperationException e) {
            throw new IOException(e);
        }

        // setListItemPatterns();
    }

    private void hackAddOperator(DrawObject doo) throws ReflectiveOperationException {
        Field operatorsField = PDFStreamEngine.class.getDeclaredField("operators");
        operatorsField.setAccessible(true);
        operators = (Map<String, OperatorProcessor>) operatorsField.get(this);

        addOp(doo);
    }

    private void addOp(GraphicsOperatorProcessor gop) {
        operators.put(gop.getName(), gop);
        gop.setContext(gse);
    }

    private void addOp2(OperatorProcessor gop) {
        operators.put(gop.getName(), gop);
        gop.setContext(gse);
    }

    /**
     * This is used to handle an operation.
     *
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     * @throws IOException If there is an error processing the operation.
     */
    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        // LOG.info("-processOperator- " + operator + " " + operands);

        String name = operator.getName();
        OperatorProcessor processor = operators.get(name);
        if (processor != null)
        {
            /*
            if ("Do".equals(name)) {
                processor.setContext(gse);
            }
            else {
                processor.setContext(this);
            }
             */
            processor.setContext(gse);
            try
            {
                processor.process(operator, operands);
            }
            catch (IOException e)
            {
                operatorException(operator, operands, e);
            }
        }
        else
        {
            unsupportedOperator(operator, operands);
        }
    }

    @Override
    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException {
        LOG.info("unsupportedOperator: " + operator);
    }

        @Override
    protected void processAnnotation(PDAnnotation annotation, PDAppearanceStream appearance) throws IOException {
        LOG.info("-processAnnotation- " + annotation + " " + appearance);
        super.processAnnotation(annotation, appearance);
    }

    @Override
    protected void processChildStream(PDContentStream contentStream, PDPage page) throws IOException {
        LOG.info("-processChildStream- " + contentStream + " " + page);
        super.processChildStream(contentStream, page);
    }

    /*
    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        // too much noise (tp)
        LOG.info("-processOperator- " + operator + " " + operands);
        super.processOperator(operator, operands);
    }

    @Override
    public void processOperator(String operator, List<COSBase> operands) throws IOException {
        // too much noise (tp)
        LOG.info("-processOperator- " + operator + " " + operands);
        super.processOperator(operator, operands);
    }
     */

    @Override
    public void processPage(PDPage page) throws IOException {
        LOG.info("-processPage- " + page);

        // ???
        gse.hackInitPage(page);

        super.processPage(page);
    }

    @Override
    protected void processSoftMask(PDTransparencyGroup group) throws IOException {
        LOG.info("-processSoftMask- " + group);
        super.processSoftMask(group);
    }

    /*
    @Override
    protected void processTilingPattern(PDTilingPattern tilingPattern, PDColor color,
                                        PDColorSpace colorSpace, Matrix patternMatrix) throws IOException {
        super.processTilingPattern(tilingPattern, color, colorSpace, patternMatrix);
    }

    @Override
    protected void processTilingPattern(PDTilingPattern tilingPattern, PDColor color,
                                        PDColorSpace colorSpace) throws IOException {
        super.processTilingPattern(tilingPattern, color, colorSpace);
    }
     */

    @Override
    protected void processTransparencyGroup(PDTransparencyGroup group) throws IOException {
        LOG.info("-processTransparencyGroup- " + group);
        super.processTransparencyGroup(group);
    }


    @Override
    public void endText() throws IOException {
        LOG.info("-endText-");
        super.endText();
    }

    @Override
    protected void endArticle() throws IOException {
        LOG.info("</article>");
        super.endArticle();
    }

    @Override
    protected void endPage(PDPage page) throws IOException {
        LOG.info("</page>");
        super.endPage(page);
    }


    @Override
    protected void startArticle() throws IOException {
        LOG.info("<article>");
        super.startArticle();
    }

    @Override
    protected void startArticle(boolean isLTR) throws IOException {
        LOG.info("<article isLTR='" + isLTR + "'>");
        super.startArticle(isLTR);
    }

    @Override
    protected void startPage(PDPage page) throws IOException {
        LOG.info("<page page='" + page + "'>");
        super.startPage(page);
    }


    @Override
    protected void processTextPosition(TextPosition pos) {
        // stripper
        /*
        super.processAnnotation();
        super.processChildStream();
        super.processOperator();
        super.processPage();
        super.processSoftMask();
        super.processTilingPattern();
        super.processTransparencyGroup();

        // engine
        super.endText();
        super.endArticle();
        super.endPage();
         */

        if (last == null) {
            LOG.info("mt: " + toString(pos));
        }
        // last != null
        else if (pos != null) {
            if (TextPositionComparator.getInstance().compare(pos, last) != 0) {
                LOG.info("mt: " + toString(pos));
                emitPart(pos);
            }
            if (last.getX() >= pos.getX()) {
                // next line
                part.append("\n");
            }
        }
        part.append(pos.getUnicode());
        super.processTextPosition(pos);

        last = pos;
    }

    private void emitPart(TextPosition last) {
        // swallow last, it will be emitted later (tp)
        // part.append(last.getUnicode());
        LOG.info("tp: " + part.toString());
        part.delete(0, part.length());
    }

    private String toString(TextPosition pos) {
        StringBuilder result = new StringBuilder("TP<");

        PDFontDescriptor fd = pos.getFont().getFontDescriptor();
        result.append(pos.getFont().getFontDescriptor().getFontFamily()).append(", ");
        result.append(pos.getFont().getFontDescriptor().getFontName()).append(", ");
        result.append(pos.getYScale()).append("pt, ");
        result.append(pos.getXScale()).append("pt, ");
        result.append(fd.getFontWeight()).append("w, ");

        // Flags
        if (fd.isFixedPitch()) {
            result.append("F");
        }
        if (fd.isAllCap()) {
            result.append("C");
        }
        if (fd.isForceBold()) {
            result.append("B");
        }
        if (fd.isItalic()) {
            result.append("I");
        }
        if (fd.isScript()) {
            result.append("s");
        }
        if (fd.isSerif()) {
            result.append("S");
        }
        if (fd.isSmallCap()) {
            result.append("K");
        }
        if (fd.isSymbolic()) {
            result.append("-sym-");
        }

        result.append(">");
        return result.toString();
    }
}
