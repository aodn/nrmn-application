"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __spreadArray = (this && this.__spreadArray) || function (to, from, pack) {
    if (pack || arguments.length === 2) for (var i = 0, l = from.length, ar; i < l; i++) {
        if (ar || !(i in from)) {
            if (!ar) ar = Array.prototype.slice.call(from, 0, i);
            ar[i] = from[i];
        }
    }
    return to.concat(ar || Array.prototype.slice.call(from));
};
Object.defineProperty(exports, "__esModule", { value: true });
// @ts-ignore
var react_1 = require("react");
var react_2 = require("@testing-library/react");
require("@testing-library/jest-dom");
var globals_1 = require("@jest/globals");
var react_router_dom_1 = require("react-router-dom");
var axiosInstance = require("../../../api/api");
var DataSheetView_1 = require("../DataSheetView");
var constants_1 = require("../../../common/constants");
require("@testing-library/jest-dom/extend-expect");
(0, globals_1.describe)('<DataSheetView/>', function () {
    var mockGetDataJob;
    var ingest = function () { };
    var columns = __spreadArray([
        'ID', 'Diver', 'Buddy', 'Site No.', 'Site Name', 'Latitude', 'Longitude', 'Date', 'Vis', 'Direction', 'Time',
        'P-Qs', 'Depth', 'Method', 'Block', 'Code', 'Species', 'Common Name', 'Total', 'Inverts'
    ], constants_1.measurements.map(function (m) { return m.fishSize; }), true);
    var extendedColumns = __spreadArray(__spreadArray([], constants_1.extendedMeasurements.map(function (m) { return m.fishSize; }), true), ['Use InvertSizing'], false);
    (0, globals_1.beforeAll)(function () {
        mockGetDataJob = jest.spyOn(axiosInstance, 'getDataJob');
        // silence errors caused by not setting an AG Grid licence
        jest.spyOn(console, 'error').mockImplementation(function () { });
    });
    (0, globals_1.afterEach)(function () {
        mockGetDataJob.mockClear();
    });
    // The export function follows the column of the grid, if the grid is right, the export fields are correct
    test('Non extend data column correct, hence export column match', function () { return __awaiter(void 0, void 0, void 0, function () {
        var canned, rerender;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    canned = require('./job16.json');
                    // Override function so that it return the data we set.
                    mockGetDataJob.mockImplementation(function (url) {
                        var raw = {
                            config: undefined,
                            data: canned,
                            headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                            status: 200,
                            statusText: url
                        };
                        return new Promise((function (resolve) {
                            resolve(raw);
                        }));
                    });
                    rerender = (0, react_2.render)(<react_router_dom_1.BrowserRouter><DataSheetView_1.default onIngest={ingest} isAdmin={false}/></react_router_dom_1.BrowserRouter>).rerender;
                    return [4 /*yield*/, (0, react_2.waitFor)(function () { return react_2.screen.findByText('user_noextend.xlsx'); })
                            .then(function () {
                            // verify default columns exist
                            columns.forEach(function (x) {
                                expect(react_2.screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
                            });
                        })
                            .finally(function () {
                            // Data loaded after initial render, need refresh to trigger HTML update
                            rerender(<react_router_dom_1.BrowserRouter><DataSheetView_1.default onIngest={ingest} isAdmin={false}/></react_router_dom_1.BrowserRouter>);
                            // non extend job and hence you will not have the following column
                            extendedColumns.forEach(function (x) {
                                expect(react_2.screen.queryAllByText(x).length).toEqual(0);
                            });
                            expect(react_2.screen.getByText('Shell substrate')).toBeInTheDocument();
                            expect(react_2.screen.getByText('Blacklip abalone')).toBeInTheDocument();
                            expect(react_2.screen.getByText('Strapweed')).toBeInTheDocument();
                        })];
                case 1:
                    _a.sent();
                    return [2 /*return*/];
            }
        });
    }); });
    // The export function follows the column of the grid, if the grid is right, the export fields are correct
    test('Extend data column correct, hence export column match', function () { return __awaiter(void 0, void 0, void 0, function () {
        var nonextend, rerender;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    nonextend = require('./job17.json');
                    // Override function so that it return the data we set.
                    mockGetDataJob.mockImplementation(function (url) {
                        var raw = {
                            config: undefined,
                            data: nonextend,
                            headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                            status: 200,
                            statusText: url
                        };
                        return new Promise((function (resolve) {
                            resolve(raw);
                        }));
                    });
                    rerender = (0, react_2.render)(<react_router_dom_1.BrowserRouter><DataSheetView_1.default onIngest={ingest} isAdmin={false}/></react_router_dom_1.BrowserRouter>).rerender;
                    return [4 /*yield*/, (0, react_2.waitFor)(function () { return react_2.screen.findByText('user_extend.xlsx'); })
                            .then(function () {
                            // verify default columns exist
                            columns.forEach(function (x) {
                                expect(react_2.screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
                            });
                        })
                            .finally(function () {
                            // Data loaded after initial render, need refresh to trigger HTML update
                            rerender(<react_router_dom_1.BrowserRouter><DataSheetView_1.default onIngest={ingest} isAdmin={false}/></react_router_dom_1.BrowserRouter>);
                            // Extend job and hence you will have the following column
                            extendedColumns.forEach(function (x) {
                                expect(react_2.screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
                            });
                            expect(react_2.screen.getByText('Interesting Bay')).toBeInTheDocument();
                            expect(react_2.screen.getByText('Boring Bay')).toBeInTheDocument();
                            expect(react_2.screen.getByText('Happy Bay')).toBeInTheDocument();
                            expect(react_2.screen.getByText('Sad Bay')).toBeInTheDocument();
                        })];
                case 1:
                    _a.sent();
                    return [2 /*return*/];
            }
        });
    }); });
});
