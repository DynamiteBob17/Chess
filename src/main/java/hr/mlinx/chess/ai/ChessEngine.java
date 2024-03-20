package hr.mlinx.chess.ai;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hr.mlinx.chess.board.*;
import hr.mlinx.chess.util.FENParser;
import hr.mlinx.chess.validation.GeneralValidator;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ChessEngine {

    private final Board board;
    private final Random random = new Random();

    public ChessEngine(Board board) {
        this.board = board;
    }

    public Move getBestMove() {
        /*Set<Move> legalMoves = getAllLegalMoves(Piece.BLACK);

        if (legalMoves.isEmpty()) {
            return null;
        }

        List<Move> moveList = new ArrayList<>(legalMoves);
        int randomIndex = random.nextInt(moveList.size());
        return moveList.get(randomIndex);*/

        String fen = FENParser.boardToFEN(board);
        String responseBody = sendGetRequest(
                "https://stockfish.online/api/s/v2.php",
                Map.of("fen", fen, "depth", "15")
        );

        JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
        String bestMove = jsonObject.get("bestmove").getAsString();

        String[] parts = bestMove.split(" ");
        String chessMove = parts[1];

        Square fromSquare = FENParser.getSquare(chessMove.substring(0, 2));
        Square toSquare = FENParser.getSquare(chessMove.substring(2));

        SpecialMove specialMove = null;
        if (isEnPassant(fromSquare, toSquare)) {
            specialMove = SpecialMove.EN_PASSANT;
        } else if (isShortCastle(fromSquare, toSquare)) {
            specialMove = SpecialMove.SHORT_CASTLE;
        } else if (isLongCastle(fromSquare, toSquare)) {
            specialMove = SpecialMove.LONG_CASTLE;
        }

        return new Move(fromSquare.row(), fromSquare.col(), toSquare.row(), toSquare.col(), specialMove);
    }

    private boolean isEnPassant(Square fromSquare, Square toSquare) {
        return Piece.getTypeFromPiece(board.getPieceAt(fromSquare.row(), fromSquare.col())) == Piece.PAWN &&
                Math.abs(fromSquare.col() - toSquare.col()) == 1 &&
                Piece.getTypeFromPiece(board.getPieceAt(toSquare.row(), toSquare.col())) == Piece.NONE;
    }

    private boolean isShortCastle(Square fromSquare, Square toSquare) {
        return isCastle(fromSquare, toSquare, true);
    }

    private boolean isLongCastle(Square fromSquare, Square toSquare) {
        return isCastle(fromSquare, toSquare, false);
    }

    private boolean isCastle(Square fromSquare, Square toSquare, boolean lookingForShortCastle) {
        return Piece.getTypeFromPiece(board.getPieceAt(fromSquare.row(), fromSquare.col())) == Piece.KING &&
                Math.abs(fromSquare.col() - toSquare.col()) == 2 &&
                toSquare.col() == (lookingForShortCastle ? 6 : 2);
    }

    public static String sendGetRequest(String requestUrl, Map<String, String> queryParams) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String encodedQueryParams = queryParams.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            String requestUrlWithQueryParams = requestUrl + (encodedQueryParams.isEmpty() ? "" : "?" + encodedQueryParams);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrlWithQueryParams))
                    .build();

            CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = responseFuture.get();

            return response.body();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Move> getAllLegalMoves(int color) {
        Set<Move> legalMoves = new HashSet<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board.getPieceAt(row, col);

                if (Piece.getColorFromPiece(piece) == color) {
                    legalMoves.addAll(GeneralValidator.getLegalMoves(row, col, board));
                }
            }
        }

        return legalMoves;
    }

    public double evaluatePosition(int color) {
        int materialScore = 0;
        int mobilityScore = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board.getPieceAt(row, col);
                int pieceColor = Piece.getColorFromPiece(piece);

                if (pieceColor != Piece.NONE) {
                    int pieceValue = getPieceValue(piece);
                    int legalMoves = 0;

                    if (pieceColor == color) {
                        materialScore += pieceValue;
                        legalMoves = GeneralValidator.getLegalMoves(row, col, board).size();
                        mobilityScore += legalMoves;
                    } else {
                        materialScore -= pieceValue;
                        legalMoves = GeneralValidator.getLegalMoves(row, col, board).size();
                        mobilityScore -= legalMoves;
                    }
                }
            }
        }

        return materialScore + (mobilityScore * WEIGHT_MOBILITY);
    }

    private int getPieceValue(int piece) {
        int pieceType = Piece.getTypeFromPiece(piece);
        return switch (pieceType) {
            case Piece.PAWN -> 1;
            case Piece.KNIGHT, Piece.BISHOP -> 3;
            case Piece.ROOK -> 5;
            case Piece.QUEEN -> 9;
            case Piece.KING -> 200;
            default -> 0;
        };
    }

    private static final double WEIGHT_MOBILITY = 0.1;

}
